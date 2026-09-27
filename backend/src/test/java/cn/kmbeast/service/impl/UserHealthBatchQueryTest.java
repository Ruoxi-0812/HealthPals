package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.mapper.*;
import cn.kmbeast.pojo.entity.UserHealth;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.*;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.*;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/** Real service + MyBatis XML + isolated H2 database; no external credentials. */
class UserHealthBatchQueryTest {
    private JdbcDataSource dataSource;
    private UserHealthServiceImpl service;
    private SqlCounter counter;
    private HealthModelConfigMapper configMapper;

    @BeforeEach
    void setup() throws Exception {
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:" + UUID.randomUUID()
                + ";MODE=MySQL;NON_KEYWORDS=USER,VALUE;DB_CLOSE_DELAY=-1");
        execute("CREATE TABLE user (id INT PRIMARY KEY, user_name VARCHAR(100))");
        execute("CREATE TABLE health_model_config (id INT PRIMARY KEY, user_id INT, name VARCHAR(100), "
                + "detail VARCHAR(100), cover VARCHAR(100), unit VARCHAR(30), symbol VARCHAR(30), "
                + "value_range VARCHAR(50), is_global BOOLEAN)");
        execute("CREATE TABLE user_health (id INT AUTO_INCREMENT PRIMARY KEY, user_id INT, "
                + "health_model_config_id INT, value VARCHAR(50), create_time TIMESTAMP)");
        execute("CREATE TABLE message (id INT AUTO_INCREMENT PRIMARY KEY, content VARCHAR(1000), "
                + "message_type INT, receiver_id INT, sender_id INT, is_read INT, content_id INT, create_time TIMESTAMP)");
        execute("INSERT INTO user VALUES (7, 'Test user')");
        for (int id = 1; id <= 100; id++) {
            execute("INSERT INTO health_model_config (id,user_id,name,unit,value_range,is_global) "
                    + "VALUES (" + id + ",7,'Metric " + id + "','unit','10,20',true)");
        }
        Configuration config = new Configuration(new Environment("test",
                new SpringManagedTransactionFactory(), dataSource));
        counter = new SqlCounter();
        config.addInterceptor(counter);
        for (String mapper : Arrays.asList("HealthModelConfigMapper", "UserHealthMapper", "MessageMapper")) {
            String resource = "mapper/" + mapper + ".xml";
            try (InputStream stream = getClass().getClassLoader().getResourceAsStream(resource)) {
                new XMLMapperBuilder(stream, config, resource, config.getSqlFragments()).parse();
            }
        }
        // Matches non-transactional production mapper calls: a fresh session per invocation.
        SqlSessionTemplate session = new SqlSessionTemplate(new SqlSessionFactoryBuilder().build(config));
        MessageServiceImpl messages = new MessageServiceImpl();
        inject(messages, "messageMapper", session.getMapper(MessageMapper.class));
        service = new UserHealthServiceImpl();
        configMapper = session.getMapper(HealthModelConfigMapper.class);
        inject(service, "healthModelConfigMapper", configMapper);
        inject(service, "userHealthMapper", session.getMapper(UserHealthMapper.class));
        inject(service, "messageService", messages);
        LocalThreadHolder.setUserId(7, 2);
    }

    @AfterEach
    void cleanup() throws Exception {
        LocalThreadHolder.clear();
        execute("DROP ALL OBJECTS");
        execute("SHUTDOWN");
    }

    @Test
    void queryCountsAcrossBatchSizes() throws Exception {
        for (int size : new int[]{1, 10, 100}) {
            execute("DELETE FROM user_health");
            execute("DELETE FROM message");
            counter.reset();
            List<UserHealth> records = new ArrayList<>();
            for (int id = 1; id <= size; id++) records.add(record(id, "21"));
            service.save(records);
            int expected = Boolean.getBoolean("batch.baseline") ? size : 1;
            assertEquals(expected, counter.selects);
            assertEquals(2, counter.writes); // One alert batch + one health record batch.
            assertEquals(size, count("user_health"));
            assertEquals(size, count("message"));
            assertEquals(size, scalar("SELECT COUNT(*) FROM user_health WHERE user_id=7 AND create_time IS NOT NULL"));
            assertEquals(size, scalar("SELECT COUNT(*) FROM message WHERE receiver_id=7"));
            System.out.printf("BATCH_METRIC records=%d config_selects=%d total_statements=%d%n",
                    size, counter.selects, counter.selects + counter.writes);
        }
    }

    @Test
    void duplicateConfigsAndRangeBoundariesPreserveAlerts() throws Exception {
        service.save(Arrays.asList(record(1, "9"), record(1, "10"), record(1, "20"), record(1, "21")));
        assertEquals(Boolean.getBoolean("batch.baseline") ? 4 : 1, counter.selects);
        assertEquals(4, count("user_health"));
        assertEquals(2, count("message"));
        assertEquals(1, scalar("SELECT COUNT(*) FROM message WHERE content LIKE '%(9 unit)%'"));
        assertEquals(1, scalar("SELECT COUNT(*) FROM message WHERE content LIKE '%(21 unit)%'"));
    }

    @Test
    void missingConfigDoesNotGenerateAlert() throws Exception {
        service.save(Arrays.asList(record(999, "25"), record(1, "15")));
        assertEquals(2, count("user_health"));
        assertEquals(0, count("message"));
        assertEquals(1, counter.writes);
    }

    // BEGIN optimized-only regression tests
    @Test
    void emptyBatchDoesNotAccessDatabase() throws Exception {
        service.save(Collections.emptyList());
        assertEquals(0, counter.selects);
        assertEquals(0, counter.writes);
        assertEquals(0, count("user_health"));
        assertEquals(0, count("message"));
    }

    @Test
    void nullConfigCannotMatchAnUnrelatedConfig() throws Exception {
        UserHealth record = record(1, "25");
        record.setHealthModelConfigId(null);
        service.save(Collections.singletonList(record));
        assertEquals(0, counter.selects);
        assertEquals(1, count("user_health"));
        assertEquals(0, count("message"));
    }

    @Test
    void emptyIdLookupCannotSelectAllConfigs() {
        assertTrue(configMapper.queryByIds(Collections.emptyList()).isEmpty());
        assertTrue(configMapper.queryByIds(null).isEmpty());
    }

    @Test
    void batchLookupReturnsOnlyRequestedExistingConfigs() {
        assertEquals(Arrays.asList(1, 3), configMapper.queryByIds(Arrays.asList(3, 1, 3, 999))
                .stream().map(config -> config.getId()).sorted().collect(java.util.stream.Collectors.toList()));
    }

    // END optimized-only regression tests

    private UserHealth record(int configId, String value) {
        UserHealth record = new UserHealth();
        record.setHealthModelConfigId(configId);
        record.setValue(value);
        record.setUserId(999); // Save must still use the authenticated user.
        return record;
    }

    private static void inject(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private void execute(String sql) throws Exception {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private int count(String table) throws Exception { return scalar("SELECT COUNT(*) FROM " + table); }

    private int scalar(String sql) throws Exception {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {
            result.next();
            return result.getInt(1);
        }
    }

    @Intercepts(@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}))
    public static class SqlCounter implements Interceptor {
        int selects;
        int writes;
        void reset() { selects = 0; writes = 0; }
        @Override
        public Object intercept(Invocation invocation) throws Throwable {
            String sql = ((StatementHandler) invocation.getTarget()).getBoundSql().getSql().trim();
            if (sql.toUpperCase(Locale.ROOT).startsWith("SELECT")) selects++; else writes++;
            return invocation.proceed();
        }
        @Override public Object plugin(Object target) { return Plugin.wrap(target, this); }
        @Override public void setProperties(Properties properties) { }
    }
}
