package cn.kmbeast.config;

import cn.kmbeast.utils.JwtUtil;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;

@Configuration
public class JwtConfiguration {
    @PostConstruct
    public void validateSigningKey() {
        // Fail startup instead of silently using a shared/default production secret.
        JwtUtil.signingKey();
    }
}
