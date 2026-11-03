package com.sixlv6.backend.auth;

import com.sixlv6.backend.config.AppProperties;
import com.sixlv6.backend.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final Duration accessTtl;

    public JwtService(AppProperties properties) {
        byte[] secret = properties.jwt().secret().getBytes(StandardCharsets.UTF_8);
        if (secret.length < 32) {
            // HS256 的密钥强度下限，弱密钥等于没有签名
            throw new IllegalStateException("app.jwt.secret 至少需要 32 字节");
        }
        this.key = Keys.hmacShaKeyFor(secret);
        this.accessTtl = properties.jwt().accessTtl();
    }

    public String issueAccessToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(accessTtl)))
                .signWith(key)
                .compact();
    }

    /**
     * 校验签名和过期时间，失败抛 JwtException（调用方决定是 401 还是忽略）。
     */
    public AuthUser parse(String token) throws JwtException {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return new AuthUser(
                Long.valueOf(claims.getSubject()),
                claims.get("username", String.class),
                claims.get("role", String.class)
        );
    }

    public long accessTtlSeconds() {
        return accessTtl.toSeconds();
    }
}
