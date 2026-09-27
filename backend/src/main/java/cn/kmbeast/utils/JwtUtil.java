package cn.kmbeast.utils;

import io.jsonwebtoken.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

public final class JwtUtil {
    private JwtUtil() { }
    private static final long TOKEN_LIFETIME_MS = 7L * 24 * 60 * 60 * 1000;

    public static byte[] signingKey() {
        String secret = System.getProperty("app.jwt.secret");
        if (secret == null) secret = System.getenv("APP_JWT_SECRET");
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("APP_JWT_SECRET must contain at least 32 UTF-8 bytes");
        }
        return secret.getBytes(StandardCharsets.UTF_8);
    }

    public static String toToken(Integer id, Integer role) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .claim("id", id).claim("role", role)
                .setSubject("Authentification of user")
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_LIFETIME_MS))
                .setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.HS256, signingKey()).compact();
    }

    public static Claims fromToken(String token) {
        if (token == null || token.trim().isEmpty()) return null;
        byte[] key = signingKey();
        try {
            Jws<Claims> parsed = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            if (!"HS256".equals(parsed.getHeader().getAlgorithm()) || parsed.getBody().getExpiration() == null) return null;
            return parsed.getBody();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}
