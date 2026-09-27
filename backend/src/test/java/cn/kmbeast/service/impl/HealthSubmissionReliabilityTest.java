package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.controller.UserHealthController;
import cn.kmbeast.Interceptor.JwtInterceptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import cn.kmbeast.mapper.*;
import cn.kmbeast.pojo.entity.UserHealth;
import cn.kmbeast.service.HealthSubmissionService;
import cn.kmbeast.service.UserHealthService;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.*;
import org.junit.jupiter.api.*;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

/** Executes real Spring transaction advice, MyBatis SQL and independent database connections. */
class HealthSubmissionReliabilityTest {
    private DataSource dataSource;
    private DataSource adminDataSource;
    private String database;
    private HealthSubmissionService service;
    private HealthSubmissionService secondInstance;
    private FailureInjector failures;
    private final String key = "request-1234567890";

    @BeforeEach
    void setup() throws Exception {
        String mysqlUrl = System.getProperty("health.test.mysqlUrl");
        database = "healthpals_test_" + UUID.randomUUID().toString().replace("-", "");
        if (mysqlUrl == null) {
            dataSource = new DriverManagerDataSource("jdbc:h2:mem:" + database
                    + ";MODE=MySQL;NON_KEYWORDS=USER,VALUE;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000", "sa", "");
        } else {
            // Never create/drop objects on remote or existing application databases.
            if (!mysqlUrl.matches("jdbc:mysql://127\\.0\\.0\\.1:[0-9]+/")) {
                throw new IllegalArgumentException("Use a disposable loopback MySQL server URL without a database");
            }
            String parameters = "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            adminDataSource = new DriverManagerDataSource(mysqlUrl + parameters, "root", "");
            execute(adminDataSource, "CREATE DATABASE " + database);
            dataSource = new DriverManagerDataSource(mysqlUrl + database + parameters, "root", "");
        }
        execute("CREATE TABLE user_health (id INT AUTO_INCREMENT PRIMARY KEY, user_id INT, "
                + "health_model_config_id INT, value VARCHAR(50), create_time TIMESTAMP)");
        execute("CREATE TABLE message (id INT AUTO_INCREMENT PRIMARY KEY, content VARCHAR(1000), "
                + "message_type INT, receiver_id INT, sender_id INT, is_read INT, content_id INT, create_time TIMESTAMP)");
        execute("CREATE TABLE health_model_config (id INT PRIMARY KEY, name VARCHAR(100), unit VARCHAR(30), value_range VARCHAR(50), user_id INT, is_global BOOLEAN)");
        execute("INSERT INTO health_model_config VALUES (1, 'Test metric', 'unit', '10,20', 7, TRUE)");
        if (mysqlUrl == null) {
            execute("CREATE TABLE health_submission (user_id INT NOT NULL, request_key VARCHAR(128) NOT NULL, "
                    + "payload_hash CHAR(64) NOT NULL, completed BOOLEAN NOT NULL DEFAULT FALSE, "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (user_id, request_key))");
        } else {
            try (Connection c = dataSource.getConnection()) {
                ScriptUtils.executeSqlScript(c, new FileSystemResource("../sql/migrations/20260922_health_submission.sql"));
            }
        }
        failures = new FailureInjector();
        service = createService();
        secondInstance = createService(); // Separate proxies/factories, sharing only the database.
        LocalThreadHolder.setUserId(7, 2);
    }

    private HealthSubmissionService createService() throws Exception {
        Configuration config = new Configuration(new Environment("test",
                new SpringManagedTransactionFactory(), dataSource));
        config.addInterceptor(failures);
        for (String mapper : Arrays.asList("HealthModelConfigMapper", "UserHealthMapper", "MessageMapper", "HealthSubmissionMapper")) {
            String resource = "mapper/" + mapper + ".xml";
            try (InputStream stream = getClass().getClassLoader().getResourceAsStream(resource)) {
                new XMLMapperBuilder(stream, config, resource, config.getSqlFragments()).parse();
            }
        }
        SqlSessionTemplate session = new SqlSessionTemplate(new SqlSessionFactoryBuilder().build(config));
        DataSourceTransactionManager transactions = new DataSourceTransactionManager(dataSource);
        MessageServiceImpl messages = new MessageServiceImpl();
        inject(messages, "messageMapper", session.getMapper(MessageMapper.class));
        UserHealthServiceImpl health = new UserHealthServiceImpl();
        inject(health, "healthModelConfigMapper", session.getMapper(HealthModelConfigMapper.class));
        inject(health, "userHealthMapper", session.getMapper(UserHealthMapper.class));
        inject(health, "messageService", messages);
        UserHealthService proxiedHealth = (UserHealthService) transactionalProxy(health, transactions);
        HealthSubmissionService submission = new HealthSubmissionService();
        inject(submission, "healthSubmissionMapper", session.getMapper(HealthSubmissionMapper.class));
        inject(submission, "userHealthService", proxiedHealth);
        return (HealthSubmissionService) transactionalProxy(submission, transactions);
    }

    private Object transactionalProxy(Object target, DataSourceTransactionManager transactions) {
        ProxyFactory factory = new ProxyFactory(target);
        factory.setProxyTargetClass(true);
        factory.addAdvice(new TransactionInterceptor(transactions, new AnnotationTransactionAttributeSource()));
        return factory.getProxy();
    }

    @AfterEach
    void cleanup() throws Exception {
        LocalThreadHolder.clear();
        if (adminDataSource != null) execute(adminDataSource, "DROP DATABASE " + database);
        else if (dataSource != null) { execute("DROP ALL OBJECTS"); execute("SHUTDOWN"); }
    }

    @Test
    void thirtyTwoConcurrentRetriesAcrossTwoInstancesWriteOneBatch() throws Exception {
        int requests = 32;
        ExecutorService pool = Executors.newFixedThreadPool(requests);
        CountDownLatch ready = new CountDownLatch(requests);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Integer>> results = new ArrayList<>();
        try {
            for (int i = 0; i < requests; i++) {
                HealthSubmissionService target = i % 2 == 0 ? service : secondInstance;
                results.add(pool.submit(() -> {
                    LocalThreadHolder.setUserId(7, 2);
                    try {
                        ready.countDown();
                        assertTrue(start.await(10, TimeUnit.SECONDS));
                        return target.submit(key, batch(10, "21")).getCode();
                    } finally { LocalThreadHolder.clear(); }
                }));
            }
            assertTrue(ready.await(10, TimeUnit.SECONDS));
            start.countDown();
            for (Future<Integer> result : results) assertEquals(200, result.get(30, TimeUnit.SECONDS));
        } finally {
            start.countDown();
            pool.shutdownNow();
            assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));
        }
        assertCounts(10, 10, 1);
        assertEquals(1, scalar("SELECT COUNT(*) FROM health_submission WHERE completed=TRUE"));
        assertEquals(1, failures.configSelects.get());
        assertEquals(68, failures.statements.get()); // 6 for the winner, 2 for each of 31 replays.
        System.out.println("RELIABILITY_METRIC concurrent_requests=32 service_instances=2 batch_size=10 records=10 alerts=10 committed_submissions=1 config_selects=1 total_statements=68");
    }

    @Test
    void replayAfterResponseLossAndServiceRecreationDoesNotDuplicate() throws Exception {
        service.submit(key, batch(1, "21"));
        createService().submit(key, batch(1, "21"));
        assertCounts(1, 1, 1);
    }

    @Test
    void sameKeyWithDifferentPayloadReturnsConflict() throws Exception {
        service.submit(key, batch(1, "21"));
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> secondInstance.submit(key, batch(1, "22")));
        assertEquals(409, error.getStatus().value());
        assertCounts(1, 1, 1);
        assertEquals(1, scalar("SELECT COUNT(*) FROM user_health WHERE value='21'"));
    }

    @Test
    void sameKeyIsScopedToAuthenticatedUser() throws Exception {
        service.submit(key, batch(1, "21"));
        LocalThreadHolder.setUserId(8, 2);
        secondInstance.submit(key, batch(1, "21"));
        assertCounts(2, 2, 2);
        assertEquals(1, scalar("SELECT COUNT(*) FROM user_health WHERE user_id=8"));
        assertEquals(1, scalar("SELECT COUNT(*) FROM message WHERE receiver_id=8"));
    }

    @Test
    void identicalReadingsWithNewKeyAreANewSubmission() throws Exception {
        service.submit(key, batch(1, "21"));
        service.submit("another-request-12345", batch(1, "21"));
        assertCounts(2, 2, 2);
    }

    @Test
    void rollbackAfterAlertInsertAllowsSameKeyRetry() throws Exception {
        rollbackAndRetry("INSERT INTO message");
    }

    @Test
    void rollbackAfterRecordInsertAllowsSameKeyRetry() throws Exception {
        rollbackAndRetry("INSERT INTO user_health");
    }

    @Test
    void rollbackAfterCompletionUpdateAllowsSameKeyRetry() throws Exception {
        rollbackAndRetry("UPDATE health_submission");
    }

    private void rollbackAndRetry(String sql) throws Exception {
        failures.arm(sql);
        assertThrows(RuntimeException.class, () -> service.submit(key, batch(10, "21")));
        assertCounts(0, 0, 0);
        secondInstance.submit(key, batch(10, "21"));
        assertCounts(10, 10, 1);
        System.out.println("ROLLBACK_METRIC failure_after=" + sql.replace(' ', '_') + " residual_rows=0 retry_succeeded=true");
    }

    @Test
    void concurrentRetrySucceedsAfterFirstTransactionRollsBack() throws Exception {
        failures.arm("INSERT INTO user_health");
        failures.entered = new CountDownLatch(1);
        failures.release = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Future<?> first = pool.submit(() -> submitAsUser(service));
            assertTrue(failures.entered.await(10, TimeUnit.SECONDS));
            Future<?> retry = pool.submit(() -> submitAsUser(secondInstance));
            // Release the failure only after the second claim has reached SQL execution.
            assertTrue(failures.competingClaim.await(10, TimeUnit.SECONDS));
            failures.release.countDown();
            assertThrows(ExecutionException.class, () -> first.get(20, TimeUnit.SECONDS));
            retry.get(20, TimeUnit.SECONDS);
            assertCounts(10, 10, 1);
        } finally {
            failures.release.countDown();
            pool.shutdownNow();
            assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));
        }
    }

    private void submitAsUser(HealthSubmissionService target) {
        LocalThreadHolder.setUserId(7, 2);
        try { target.submit(key, batch(10, "21")); }
        finally { LocalThreadHolder.clear(); }
    }

    @Test
    void invalidInputDoesNotClaimKeyOrWriteData() throws Exception {
        assertThrows(ResponseStatusException.class, () -> service.submit("short", batch(1, "21")));
        assertThrows(ResponseStatusException.class, () -> service.submit(key, batch(101, "21")));
        assertThrows(ResponseStatusException.class, () -> service.submit(key, batch(0, "21")));
        for (String value : Arrays.asList("NaN", "Infinity", "not-a-number", "")) {
            assertThrows(ResponseStatusException.class, () -> service.submit(key, batch(1, value)));
        }
        LocalThreadHolder.clear();
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.submit(key, batch(1, "21")));
        assertEquals(401, error.getStatus().value());
        assertCounts(0, 0, 0);
    }

    @Test
    void callerOwnershipAndTimestampAreIgnoredForReplay() throws Exception {
        List<UserHealth> original = batch(1, "21");
        original.get(0).setUserId(999);
        service.submit(key, original);
        List<UserHealth> retry = batch(1, "21");
        retry.get(0).setUserId(123);
        retry.get(0).setCreateTime(java.time.LocalDateTime.of(2000, 1, 1, 0, 0));
        secondInstance.submit(key, retry);
        assertCounts(1, 1, 1);
        assertEquals(1, scalar("SELECT COUNT(*) FROM user_health WHERE user_id=7"));
    }

    @Test
    void httpContractRequiresKeyAndReturnsConflictForChangedPayload() throws Exception {
        UserHealthController controller = new UserHealthController();
        inject(controller, "healthSubmissionService", service);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();
        String body = "[{\"healthModelConfigId\":1,\"value\":\"21\"}]";
        assertEquals(400, mvc.perform(post("/user-health/save").contentType("application/json")
                .content(body)).andReturn().getResponse().getStatus());
        for (int i = 0; i < 2; i++) {
            assertEquals(200, mvc.perform(post("/user-health/save").header("Idempotency-Key", key)
                    .contentType("application/json").content(body)).andReturn().getResponse().getStatus());
        }
        assertEquals(409, mvc.perform(post("/user-health/save").header("Idempotency-Key", key)
                .contentType("application/json").content(body.replace("21", "22")))
                .andReturn().getResponse().getStatus());
        assertCounts(1, 1, 1);
    }

    @Test
    void authenticationContextDoesNotLeakAcrossReusedRequestThreads() throws Exception {
        JwtInterceptor interceptor = new JwtInterceptor();
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/user-health/save");
        assertFalse(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
        assertNull(LocalThreadHolder.getUserId());
        LocalThreadHolder.setUserId(7, 2);
        interceptor.afterCompletion(request, new MockHttpServletResponse(), new Object(), new RuntimeException());
        assertNull(LocalThreadHolder.getUserId());
    }

    private List<UserHealth> batch(int count, String value) {
        List<UserHealth> records = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            UserHealth record = new UserHealth();
            record.setHealthModelConfigId(1);
            record.setValue(value);
            records.add(record);
        }
        return records;
    }

    private void assertCounts(int records, int alerts, int submissions) throws Exception {
        assertEquals(records, scalar("SELECT COUNT(*) FROM user_health"));
        assertEquals(alerts, scalar("SELECT COUNT(*) FROM message"));
        assertEquals(submissions, scalar("SELECT COUNT(*) FROM health_submission"));
    }

    private static void inject(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
    private void execute(String sql) throws Exception { execute(dataSource, sql); }
    private void execute(DataSource source, String sql) throws Exception {
        try (Connection c = source.getConnection(); Statement s = c.createStatement()) { s.execute(sql); }
    }
    private int scalar(String sql) throws Exception {
        try (Connection c = dataSource.getConnection(); Statement s = c.createStatement(); ResultSet r = s.executeQuery(sql)) {
            r.next(); return r.getInt(1);
        }
    }

    @Intercepts({
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
    })
    public static class FailureInjector implements Interceptor {
        final AtomicInteger configSelects = new AtomicInteger();
        final AtomicInteger statements = new AtomicInteger();
        private volatile String target;
        private final AtomicBoolean armed = new AtomicBoolean();
        volatile CountDownLatch entered;
        volatile CountDownLatch release;
        final CountDownLatch competingClaim = new CountDownLatch(1);
        void arm(String sql) { target = sql; armed.set(true); }
        @Override public Object intercept(Invocation invocation) throws Throwable {
            String sql = ((StatementHandler) invocation.getTarget()).getBoundSql().getSql().trim();
            if ("prepare".equals(invocation.getMethod().getName())) {
                statements.incrementAndGet();
                if (sql.contains("FROM health_model_config")) configSelects.incrementAndGet();
                return invocation.proceed();
            }
            if (sql.startsWith("INSERT INTO health_submission") && entered != null && entered.getCount() == 0) {
                competingClaim.countDown();
            }
            Object result = invocation.proceed(); // Fail AFTER the write really executed.
            if (target != null && sql.startsWith(target) && armed.compareAndSet(true, false)) {
                if (entered != null) {
                    entered.countDown();
                    if (!release.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("Failure release timed out");
                }
                throw new IllegalStateException("Injected failure after " + target);
            }
            return result;
        }
        @Override public Object plugin(Object target) { return Plugin.wrap(target, this); }
        @Override public void setProperties(Properties properties) { }
    }
}
