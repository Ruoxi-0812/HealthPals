package cn.kmbeast.security;

import cn.kmbeast.Interceptor.JwtInterceptor;
import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.controller.*;
import cn.kmbeast.mapper.*;
import cn.kmbeast.service.*;
import cn.kmbeast.service.impl.*;
import cn.kmbeast.utils.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.*;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.http.HttpMethod;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

/** Full HTTP -> real JWT interceptor -> services/transactions -> real MyBatis SQL. */
class AccessControlTest {
    private javax.sql.DataSource db;
    private javax.sql.DataSource adminDb;
    private String database;
    @org.junit.jupiter.api.io.TempDir java.nio.file.Path uploadDir;
    private MockMvc mvc;
    private final ObjectMapper json = new ObjectMapper();
    private String user;
    private String other;
    private String admin;
    private String previousSecret;

    @BeforeEach
    void setup() throws Exception {
        previousSecret=System.getProperty("app.jwt.secret");
        System.setProperty("app.jwt.secret",UUID.randomUUID().toString()+UUID.randomUUID());
        database="healthpals_test_"+UUID.randomUUID().toString().replace("-", "");
        String mysql=System.getProperty("health.test.mysqlUrl");
        if(mysql==null) {
            JdbcDataSource h2=new JdbcDataSource();
            h2.setURL("jdbc:h2:mem:"+database+";MODE=MySQL;NON_KEYWORDS=USER,VALUE;DB_CLOSE_DELAY=-1");
            db=h2;
        } else {
            if(!mysql.matches("jdbc:mysql://127\\.0\\.0\\.1:[0-9]+/")) throw new IllegalArgumentException("Disposable loopback server required");
            String options="?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            adminDb=new org.springframework.jdbc.datasource.DriverManagerDataSource(mysql+options,"root","");
            try(Connection c=adminDb.getConnection();Statement stmt=c.createStatement()){stmt.execute("CREATE DATABASE "+database);}
            db=new org.springframework.jdbc.datasource.DriverManagerDataSource(mysql+database+options,"root","");
        }
        sql("CREATE TABLE user (id INT PRIMARY KEY,user_account VARCHAR(50),user_name VARCHAR(50),user_pwd VARCHAR(50),user_avatar VARCHAR(50),user_email VARCHAR(50),user_role INT,is_login BOOLEAN,is_word BOOLEAN,create_time TIMESTAMP)");
        sql("INSERT INTO user VALUES (7,'a','Alice','test',NULL,'a@example.invalid',2,FALSE,FALSE,CURRENT_TIMESTAMP),(8,'b','Bob','test',NULL,'b@example.invalid',2,FALSE,FALSE,CURRENT_TIMESTAMP),(1,'admin','Admin','test',NULL,NULL,1,FALSE,FALSE,CURRENT_TIMESTAMP)");
        sql("CREATE TABLE health_model_config (id INT AUTO_INCREMENT PRIMARY KEY,user_id INT,name VARCHAR(50),detail VARCHAR(50),cover VARCHAR(50),unit VARCHAR(30),symbol VARCHAR(30),value_range VARCHAR(50),is_global BOOLEAN)");
        sql("INSERT INTO health_model_config (id,user_id,name,unit,value_range,is_global) VALUES (1,1,'Global','unit','10,20',TRUE),(7,7,'Alice model','unit','10,20',FALSE),(8,8,'Bob model','unit','10,20',FALSE)");
        sql("CREATE TABLE user_health (id INT AUTO_INCREMENT PRIMARY KEY,user_id INT,health_model_config_id INT,value VARCHAR(50),create_time TIMESTAMP)");
        sql("INSERT INTO user_health VALUES (7,7,1,'15',CURRENT_TIMESTAMP),(8,8,1,'16',CURRENT_TIMESTAMP)");
        sql("CREATE TABLE news (id INT AUTO_INCREMENT PRIMARY KEY,tag_id INT,name VARCHAR(100),content VARCHAR(100),cover VARCHAR(100),create_time TIMESTAMP)");
        sql("INSERT INTO news VALUES (1,1,'Article','Text',NULL,CURRENT_TIMESTAMP)");
        sql("CREATE TABLE tags (id INT AUTO_INCREMENT PRIMARY KEY,name VARCHAR(100))");
        sql("INSERT INTO tags VALUES(1,'Tag')");
        sql("CREATE TABLE news_save(id INT AUTO_INCREMENT PRIMARY KEY,user_id INT,news_id INT,create_time TIMESTAMP)");
        sql("INSERT INTO news_save VALUES(7,7,1,CURRENT_TIMESTAMP),(8,8,1,CURRENT_TIMESTAMP)");
        sql("CREATE TABLE evaluations(id INT AUTO_INCREMENT PRIMARY KEY,parent_id INT,commenter_id INT,replier_id INT,content_type VARCHAR(50),content_id INT,content VARCHAR(100),upvote_list VARCHAR(100),create_time TIMESTAMP)");
        sql("INSERT INTO evaluations VALUES(7,NULL,7,NULL,'news',1,'Alice comment',NULL,CURRENT_TIMESTAMP),(8,NULL,8,NULL,'news',1,'Bob comment',NULL,CURRENT_TIMESTAMP)");
        sql("CREATE TABLE message(id INT AUTO_INCREMENT PRIMARY KEY,content VARCHAR(1000),message_type INT,receiver_id INT,sender_id INT,is_read INT,content_id INT,create_time TIMESTAMP)");
        sql("INSERT INTO message VALUES(7,'Alice message',1,7,8,0,8,CURRENT_TIMESTAMP),(8,'Bob message',1,8,7,0,7,CURRENT_TIMESTAMP)");
        sql("CREATE TABLE health_submission(user_id INT,request_key VARCHAR(128),payload_hash CHAR(64),completed BOOLEAN,PRIMARY KEY(user_id,request_key))");
        Configuration config = new Configuration(new Environment("test",new SpringManagedTransactionFactory(),db));
        String[] names = {"User", "UserHealth", "HealthModelConfig", "Message", "News", "NewsSave", "Tags", "Evaluations", "Ownership", "HealthSubmission"};
        for (String name : names) {
            String resource="mapper/"+name+"Mapper.xml";
            try (InputStream in=getClass().getClassLoader().getResourceAsStream(resource)) {
                new XMLMapperBuilder(in,config,resource,config.getSqlFragments()).parse();
            }
        }
        SqlSessionTemplate session = new SqlSessionTemplate(new SqlSessionFactoryBuilder().build(config));
        Map<Class<?>,Object> dependencies = new HashMap<>();
        for (String name : names) {
            Class<?> type=Class.forName("cn.kmbeast.mapper."+name+"Mapper");
            dependencies.put(type,session.getMapper(type));
        }
        OwnershipGuard guard=new OwnershipGuard();
        wire(guard,dependencies);
        dependencies.put(OwnershipGuard.class,guard);
        Object[] services={new MessageServiceImpl(),new UserHealthServiceImpl(),new HealthModelConfigServiceImpl(),new NewsSaveServiceImpl(),new EvaluationsServiceImpl(),new UserServiceImpl(),new NewsServiceImpl(),new TagsServiceImpl(),new ViewsServiceImpl(),new HealthSubmissionService()};
        for (Object target: services) {
            ProxyFactory factory=new ProxyFactory(target);
            factory.setProxyTargetClass(true);
            factory.addAdvice(new TransactionInterceptor(new DataSourceTransactionManager(db),new AnnotationTransactionAttributeSource()));
            Object proxy=factory.getProxy();
            dependencies.put(target.getClass(),proxy);
            for (Class<?> type:target.getClass().getInterfaces()) dependencies.put(type,proxy);
        }
        for (Object target:services) wire(target,dependencies);
        Object[] controllers={new UserController(),new UserHealthController(),new HealthModelConfigController(),new MessageController(),new NewsSaveController(),new EvaluationsController(),new NewsController(),new TagsController(),new ViewsController(),new HealthCheckController(),new HealthAssistantController(),new FileController()};
        for (Object controller:controllers) wire(controller,dependencies);
        for(Object controller:controllers) if(controller instanceof FileController) {
            Field dir=FileController.class.getDeclaredField("uploadDir");dir.setAccessible(true);dir.set(controller,uploadDir.toString());
            Field api=FileController.class.getDeclaredField("API");api.setAccessible(true);api.set(controller,"");
        }
        mvc=MockMvcBuilders.standaloneSetup(controllers).addInterceptors(new JwtInterceptor()).build();
        user=JwtUtil.toToken(7,2); other=JwtUtil.toToken(8,2); admin=JwtUtil.toToken(1,1);
    }
    @AfterEach void cleanup() throws Exception {
        LocalThreadHolder.clear();
        if(previousSecret==null) System.clearProperty("app.jwt.secret"); else System.setProperty("app.jwt.secret",previousSecret);
        if(adminDb!=null) {try(Connection c=adminDb.getConnection();Statement stmt=c.createStatement()){stmt.execute("DROP DATABASE "+database);}}
        else {sql("DROP ALL OBJECTS");sql("SHUTDOWN");}
    }

    @Test void authenticatedUploadsStillWorkAndMediaReadsRemainPublic() throws Exception {
        org.springframework.mock.web.MockMultipartFile file=new org.springframework.mock.web.MockMultipartFile("file","icon.png","image/png",new byte[]{1,2,3});
        assertEquals(401,mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart("/file/upload").file(file)).andReturn().getResponse().getStatus());
        MvcResult result=mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart("/file/upload").file(file).header("token",user)).andReturn();
        assertEquals(200,body(result).get("code").asInt());
        String url=body(result).get("data").asText();
        assertEquals(200,call("GET",url,null,null).getResponse().getStatus());
    }
    @Test void forgedExpiredAndUnsignedTokensAreRejected() throws Exception {
        byte[] untrustedKey="untrusted-key-for-security-test-only-123456789".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String forged=io.jsonwebtoken.Jwts.builder().claim("id",7).claim("role",1)
                .setExpiration(new java.util.Date(System.currentTimeMillis()+60000))
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256,untrustedKey).compact();
        String expired=io.jsonwebtoken.Jwts.builder().claim("id",7).claim("role",1)
                .setExpiration(new java.util.Date(System.currentTimeMillis()-60000))
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256,JwtUtil.signingKey()).compact();
        String unsigned=io.jsonwebtoken.Jwts.builder().claim("id",7).claim("role",1).compact();
        String[] pieces=user.split("\\.");
        byte[] payload=Base64.getUrlDecoder().decode(pieces[1]);
        String altered=new String(payload,java.nio.charset.StandardCharsets.UTF_8).replace("\"role\":2","\"role\":1");
        String tampered=pieces[0]+"."+Base64.getUrlEncoder().withoutPadding().encodeToString(altered.getBytes(java.nio.charset.StandardCharsets.UTF_8))+"."+pieces[2];
        for(String token:Arrays.asList(forged,expired,unsigned,tampered)) {
            assertEquals(401,call("POST","/user/query",token,"{}").getResponse().getStatus());
        }
    }
    @Test void weakSigningSecretFailsConfigurationValidation() {
        System.setProperty("app.jwt.secret","too-short");
        assertThrows(IllegalStateException.class,()->new cn.kmbeast.config.JwtConfiguration().validateSigningKey());
    }
    @Test void exactPublicPathsDoNotExposeSimilarlyNamedBusinessRoutes() throws Exception {
        assertEquals(200,call("GET","/health",null,null).getResponse().getStatus());
        for(String path:Arrays.asList("/health-model-config/query","/health-assistant/chat","/file/upload","/file/video/upload","/user-health/query")) {
            assertEquals(401,call("POST",path,null,"{}").getResponse().getStatus(),path);
        }
        assertEquals(401,call("POST","/user-health/query","invalid","{}").getResponse().getStatus());
        assertEquals(401,call("POST","/user-health/query",JwtUtil.toToken(7,999),"{}").getResponse().getStatus());
    }
    @Test void publicPathMatchingWorksWithServletContextAndHttpMethod() throws Exception {
        JwtInterceptor interceptor=new JwtInterceptor();
        for(String path:Arrays.asList("/health","/user/login","/file/getFile")) {
            String method=path.equals("/user/login")?"POST":"GET";
            MockHttpServletRequest req=new MockHttpServletRequest(method,"/api/personal-heath/v1.0"+path);
            req.setContextPath("/api/personal-heath/v1.0");
            assertTrue(interceptor.preHandle(req,new MockHttpServletResponse(),new Object()));
        }
        MockHttpServletRequest req=new MockHttpServletRequest("POST","/health");
        MockHttpServletResponse response=new MockHttpServletResponse();
        assertFalse(interceptor.preHandle(req,response,new Object())); assertEquals(401,response.getStatus());
    }
    @Test void ordinaryUsersCannotInvokeAdministrativeRoutes() throws Exception {
        String[][] routes={{"POST","/news/save"},{"PUT","/news/update"},{"POST","/news/batchDelete"},{"POST","/tags/save"},{"PUT","/tags/update"},{"POST","/tags/batchDelete"},{"POST","/user/insert"},{"PUT","/user/backUpdate"},{"POST","/user/batchDelete"},{"POST","/user/query"},{"GET","/user/daysQuery/7"},{"GET","/views/staticControls"},{"POST","/message/systemInfoUsersSave"},{"POST","/message/systemInfoSave"},{"POST","/evaluations/query"},{"POST","/evaluations/batchDelete"},{"POST","/health-model-config/config/save"}};
        for(String[] route:routes) assertEquals(403,call(route[0],route[1],user,"{}").getResponse().getStatus(),route[1]);
        assertEquals(1,count("news")); assertEquals(2,count("message"));
        assertNull(LocalThreadHolder.getUserId());
    }
    @Test void adminCanCreateGlobalModelsAndManageContent() throws Exception {
        ok("POST","/health-model-config/config/save",admin,"{\"name\":\"New global\",\"valueRange\":\"10,20\"}");
        assertEquals(2,scalar("SELECT COUNT(*) FROM health_model_config WHERE is_global=TRUE"));
        ok("PUT","/news/update",admin,"{\"id\":1,\"name\":\"Updated\"}");
        assertEquals(1,scalar("SELECT COUNT(*) FROM news WHERE name='Updated'"));
        ok("POST","/user/query",admin,"{}");
    }
    @Test void healthQueriesIgnoreForgedUserIdAndScopeCounts() throws Exception {
        JsonNode result=body(ok("POST","/user-health/query",user,"{\"userId\":8}"));
        assertEquals(1,result.get("total").asInt()); assertEquals(7,result.get("data").get(0).get("userId").asInt());
        assertEquals(2,body(ok("POST","/user-health/query",admin,"{}")).get("total").asInt());
    }
    @Test void healthUpdatesRejectOtherOwnersButAllowOwnerAndAdmin() throws Exception {
        assertEquals(403,call("PUT","/user-health/update",user,"{\"id\":8,\"userId\":7,\"value\":\"99\"}").getResponse().getStatus());
        ok("PUT","/user-health/update",user,"{\"id\":7,\"value\":\"17\"}");
        ok("PUT","/user-health/update",admin,"{\"id\":8,\"value\":\"18\"}");
        assertEquals(1,scalar("SELECT COUNT(*) FROM user_health WHERE id=8 AND value='18'"));
    }
    @Test void mixedOwnerHealthDeleteRollsBackEntireBatch() throws Exception {
        assertEquals(403,call("POST","/user-health/batchDelete",user,"[7,8]").getResponse().getStatus());
        assertEquals(2,count("user_health"));
        ok("POST","/user-health/batchDelete",user,"[7]"); assertEquals(1,count("user_health"));
        ok("POST","/user-health/batchDelete",admin,"[8]"); assertEquals(0,count("user_health"));
    }
    @Test void bookmarkQueriesAndWritesAreScopedToCaller() throws Exception {
        assertEquals(7,body(ok("POST","/news-save/query",user,"{\"userId\":8}")).get("data").get(0).get("userId").asInt());
        ok("POST","/news-save/save",user,"{\"userId\":8,\"newsId\":1}");
        assertEquals(2,scalar("SELECT COUNT(*) FROM news_save WHERE user_id=7"));
        assertEquals(403,call("POST","/news-save/batchDelete",user,"[7,8]").getResponse().getStatus());
        assertEquals(3,count("news_save"));
        ok("POST","/news-save/batchDelete",user,"[7]");
    }
    @Test void messageQueriesDeletesAndReadFlagsAreScoped() throws Exception {
        assertEquals(7,body(ok("POST","/message/query",user,"{\"userId\":8}")).get("data").get(0).get("receiverId").asInt());
        assertEquals(403,call("POST","/message/batchDelete",user,"[7,8]").getResponse().getStatus());
        assertEquals(2,count("message"));
        ok("PUT","/message/clearMessage",user,null);
        assertEquals(0,scalar("SELECT is_read FROM message WHERE id=8"));
        ok("POST","/message/batchDelete",user,"[7]"); assertEquals(1,count("message"));
    }
    @Test void modelQueriesExposeOnlyGlobalAndOwnedModels() throws Exception {
        JsonNode result=body(ok("POST","/health-model-config/query",user,"{\"visibleTo\":8}"));
        assertEquals(2,result.get("total").asInt());
        assertEquals(1,body(ok("POST","/health-model-config/query",user,"{\"isGlobal\":true}")).get("total").asInt());
        for(JsonNode model:result.get("data")) assertNotEquals(8,model.get("id").asInt());
        assertEquals(0,body(ok("POST","/health-model-config/query",user,"{\"userId\":8}")).get("total").asInt());
    }
    @Test void privateModelCreationCannotForgeOwnerOrGlobalFlag() throws Exception {
        ok("POST","/health-model-config/save",user,"{\"name\":\"Private\",\"userId\":8,\"isGlobal\":true,\"valueRange\":\"10,20\"}");
        assertEquals(1,scalar("SELECT COUNT(*) FROM health_model_config WHERE name='Private' AND user_id=7 AND is_global=FALSE"));
    }
    @Test void modelWritesRejectGlobalAndOtherOwners() throws Exception {
        for(int id:new int[]{1,8}) {
            assertEquals(403,call("PUT","/health-model-config/update",user,"{\"id\":"+id+",\"name\":\"Bad\"}").getResponse().getStatus());
            assertEquals(403,call("POST","/health-model-config/batchDelete",user,"[7,"+id+"]").getResponse().getStatus());
        }
        assertEquals(3,count("health_model_config"));
        ok("PUT","/health-model-config/update",user,"{\"id\":7,\"name\":\"Mine\"}");
        ok("PUT","/health-model-config/update",admin,"{\"id\":1,\"name\":\"Global updated\"}");
    }
    @Test void healthSubmissionCannotUseAnotherUsersPrivateModel() throws Exception {
        MockHttpServletRequestBuilder req=request(HttpMethod.POST,"/user-health/save").header("token",user).header("Idempotency-Key","authorization-test-123").contentType("application/json").content("[{\"healthModelConfigId\":8,\"value\":\"25\"}]");
        assertEquals(403,mvc.perform(req).andReturn().getResponse().getStatus());
        assertEquals(2,count("user_health")); assertEquals(2,count("message")); assertEquals(0,count("health_submission"));
    }
    @Test void commentDeletionRequiresOwnerOrAdmin() throws Exception {
        assertEquals(403,call("DELETE","/evaluations/delete/8",user,null).getResponse().getStatus());
        ok("DELETE","/evaluations/delete/7",user,null);
        ok("DELETE","/evaluations/delete/8",admin,null); assertEquals(0,count("evaluations"));
    }
    @Test void votingOnOtherUsersCommentsIsAllowedButVoterListCannotBeForged() throws Exception {
        JsonNode result=body(ok("PUT","/evaluations/update",user,"{\"id\":8,\"upvoteList\":\"999,888\"}"));
        assertEquals(1,result.get("data").get("num").asInt());
        assertEquals(1,scalar("SELECT COUNT(*) FROM evaluations WHERE id=8 AND upvote_list='7'"));
        result=body(ok("PUT","/evaluations/update",user,"{\"id\":8,\"upvoteList\":\"999\"}"));
        assertEquals(0,result.get("data").get("num").asInt());
        assertFalse(result.get("data").get("flag").asBoolean());
    }
    @Test void profileLookupRejectsOtherUsersButAllowsAdmin() throws Exception {
        assertEquals(403,call("GET","/user/getById/8",user,null).getResponse().getStatus());
        ok("GET","/user/getById/7",user,null); ok("GET","/user/getById/8",admin,null);
    }
    @Test void invalidBatchIdsCannotBypassOwnershipChecks() throws Exception {
        for(String ids:Arrays.asList("[]","[null]","[0]")) assertEquals(400,call("POST","/user-health/batchDelete",user,ids).getResponse().getStatus());
        assertEquals(403,call("POST","/user-health/batchDelete",user,"[999]").getResponse().getStatus());
        assertEquals(2,count("user_health"));
    }
    @Test void requestIdentityIsClearedAfterSuccessAndFailure() throws Exception {
        ok("POST","/user-health/query",other,"{}"); assertNull(LocalThreadHolder.getUserId());
        call("PUT","/user-health/update",user,"{\"id\":8,\"value\":\"99\"}"); assertNull(LocalThreadHolder.getUserId());
        assertEquals(401,call("POST","/user-health/query",null,"{}").getResponse().getStatus());
    }
    private MvcResult call(String method,String path,String token,String body) throws Exception {
        MockHttpServletRequestBuilder req=request(HttpMethod.valueOf(method),path);
        if(token!=null) req.header("token",token);
        if(body!=null) req.contentType("application/json").content(body);
        return mvc.perform(req).andReturn();
    }
    private MvcResult ok(String method,String path,String token,String body) throws Exception {
        MvcResult result=call(method,path,token,body); assertEquals(200,result.getResponse().getStatus(),result.getResponse().getContentAsString()); return result;
    }
    private JsonNode body(MvcResult result) throws Exception { return json.readTree(result.getResponse().getContentAsString()); }
    private void wire(Object target,Map<Class<?>,Object> dependencies) throws Exception {
        for(Field f:target.getClass().getDeclaredFields()) if(dependencies.containsKey(f.getType())) {f.setAccessible(true);f.set(target,dependencies.get(f.getType()));}
    }
    private void sql(String sql) throws Exception {try(Connection c=db.getConnection();Statement s=c.createStatement()){s.execute(sql);}}
    private int count(String table) throws Exception {return scalar("SELECT COUNT(*) FROM "+table);}
    private int scalar(String sql) throws Exception {try(Connection c=db.getConnection();Statement s=c.createStatement();ResultSet r=s.executeQuery(sql)){r.next();return r.getInt(1);}}
}
