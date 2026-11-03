package com.sixlv6.backend.auth;

import com.sixlv6.backend.auth.dto.AccessToken;
import com.sixlv6.backend.auth.dto.AuthResult;
import com.sixlv6.backend.auth.dto.LoginRequest;
import com.sixlv6.backend.auth.dto.RegisterRequest;
import com.sixlv6.backend.auth.dto.UserDto;
import com.sixlv6.backend.common.BusinessException;
import com.sixlv6.backend.config.AppProperties;
import com.sixlv6.backend.user.User;
import com.sixlv6.backend.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class AuthService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository users;
    private final RefreshTokenRepository refreshTokens;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AppProperties properties;

    public AuthService(UserRepository users,
                       RefreshTokenRepository refreshTokens,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AppProperties properties) {
        this.users = users;
        this.refreshTokens = refreshTokens;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.properties = properties;
    }

    @Transactional
    public Session register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (users.existsByUsernameIgnoreCase(request.username())) {
            throw new BusinessException(40901, "用户名已被占用", HttpStatus.CONFLICT);
        }
        if (users.existsByEmailIgnoreCase(email)) {
            throw new BusinessException(40902, "邮箱已被注册", HttpStatus.CONFLICT);
        }

        User user = new User();
        user.setUsername(request.username().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        users.save(user);

        return issue(user);
    }

    @Transactional
    public Session login(LoginRequest request) {
        String account = request.account().trim();
        User user = (account.contains("@")
                ? users.findByEmailIgnoreCase(account)
                : users.findByUsernameIgnoreCase(account))
                .orElse(null);

        // 用户不存在时也做一次哈希比对，避免用响应时间探测账号是否存在
        boolean matched = user != null
                && passwordEncoder.matches(request.password(), user.getPasswordHash());
        if (!matched) {
            throw new BusinessException(40101, "账号或密码不正确", HttpStatus.UNAUTHORIZED);
        }
        if (user.getStatus() != 1) {
            throw new BusinessException(40301, "账号已被禁用", HttpStatus.FORBIDDEN);
        }
        return issue(user);
    }

    /**
     * 刷新令牌轮换：旧令牌立即作废，换发新的一对。
     * 如果收到一个已作废的令牌，认为是泄露重放，直接踢掉该用户所有会话。
     */
    @Transactional
    public Session refresh(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new BusinessException(40102, "登录状态已失效，请重新登录", HttpStatus.UNAUTHORIZED);
        }
        RefreshToken stored = refreshTokens.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new BusinessException(40102, "登录状态已失效，请重新登录", HttpStatus.UNAUTHORIZED));

        if (stored.isRevoked()) {
            refreshTokens.revokeAllByUserId(stored.getUserId());
            throw new BusinessException(40103, "检测到令牌重放，已退出全部设备", HttpStatus.UNAUTHORIZED);
        }
        if (stored.getExpiresAt().isBefore(Instant.now())) {
            stored.setRevoked(true);
            throw new BusinessException(40102, "登录状态已失效，请重新登录", HttpStatus.UNAUTHORIZED);
        }

        User user = users.findById(stored.getUserId())
                .orElseThrow(() -> new BusinessException(40102, "账号不存在", HttpStatus.UNAUTHORIZED));

        stored.setRevoked(true);
        return issue(user);
    }

    @Transactional
    public void logout(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return;
        }
        refreshTokens.findByTokenHash(hash(rawToken)).ifPresent(token -> token.setRevoked(true));
    }

    @Transactional(readOnly = true)
    public UserDto me(Long userId) {
        User user = users.findById(userId)
                .orElseThrow(() -> new BusinessException(40401, "用户不存在", HttpStatus.NOT_FOUND));
        return toDto(user);
    }

    private Session issue(User user) {
        String accessToken = jwtService.issueAccessToken(user);
        String rawRefresh = newRefreshToken();
        Duration refreshTtl = properties.jwt().refreshTtl();

        RefreshToken token = new RefreshToken();
        token.setUserId(user.getId());
        token.setTokenHash(hash(rawRefresh));
        token.setExpiresAt(Instant.now().plus(refreshTtl));
        refreshTokens.save(token);

        // 顺手清理过期记录，量小可随时调用
        refreshTokens.deleteExpiredBefore(Instant.now().minus(refreshTtl));

        AuthResult result = new AuthResult(
                AccessToken.bearer(accessToken, jwtService.accessTtlSeconds()),
                toDto(user)
        );
        return new Session(result, rawRefresh, refreshTtl);
    }

    private static String newRefreshToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String hash(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }

    public record Session(AuthResult result, String refreshToken, Duration refreshTtl) {
    }
}
