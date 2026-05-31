package cn.kmbeast.config;

import cn.kmbeast.Interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * API Interceptor Configuration
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Value("${my-server.api-context-path}")
    private String API;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register the interceptor
        registry.addInterceptor(new JwtInterceptor())
                .addPathPatterns("/**")
                // Allow login, registration, and file-related requests to pass through
                .excludePathPatterns(
                        API + "/user/login",
                        API + "/user/google-login",
                        API + "/user/register",
                        API + "/health",
                        API + "/file/upload",
                        API + "/file/getFile"
                );
    }
}
