package cn.kmbeast.security;
import cn.kmbeast.service.impl.UserServiceImpl;
import cn.kmbeast.mapper.UserMapper;
import cn.kmbeast.pojo.entity.User;
import cn.kmbeast.pojo.dto.update.GoogleLoginDTO;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.junit.jupiter.api.Test;
import java.lang.reflect.*;
import static org.junit.jupiter.api.Assertions.*;
class GoogleAccountPolicyTest {
    @Test void blockedGoogleAccountCannotLogin() throws Exception {
        UserServiceImpl service=service(true,true);
        GoogleLoginDTO request=new GoogleLoginDTO();request.setIdToken("test-only-token");
        assertEquals(400,service.googleLogin(request).getCode().intValue());
        assertEquals("Abnormal login status",service.googleLogin(request).getMsg());
    }
    @Test void unverifiedGoogleEmailIsRejectedBeforeLookup() throws Exception {
        UserServiceImpl service=service(false,false);
        GoogleLoginDTO request=new GoogleLoginDTO();request.setIdToken("test-only-token");
        assertEquals("Google credential is not verified",service.googleLogin(request).getMsg());
    }
    private UserServiceImpl service(boolean verified,boolean blocked) throws Exception {
        UserServiceImpl service=new UserServiceImpl();
        GoogleIdToken token=new GoogleIdToken(new com.google.api.client.json.webtoken.JsonWebSignature.Header(),
                new GoogleIdToken.Payload().setSubject("subject").setEmailVerified(verified),new byte[0],new byte[0]);
        GoogleIdTokenVerifier verifier=new GoogleIdTokenVerifier(new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),GsonFactory.getDefaultInstance())) {
            @Override public GoogleIdToken verify(String ignored) { return token; }
        };
        set(service,"googleClientId","test-client");set(service,"googleVerifier",verifier);
        set(service,"userMapper",Proxy.newProxyInstance(UserMapper.class.getClassLoader(),new Class[]{UserMapper.class},(p,m,a)->{
            if(!verified) fail("unverified identity must not query users");
            if(m.getName().equals("getByActive")) return User.builder().id(7).userRole(2).isLogin(blocked).build();
            throw new AssertionError("unexpected write");
        }));
        return service;
    }
    private void set(Object object,String name,Object value) throws Exception {
        Field field=object.getClass().getDeclaredField(name);field.setAccessible(true);field.set(object,value);
    }
}
