package com.sixlv6.backend.auth;

import com.sixlv6.backend.auth.dto.AuthResult;
import com.sixlv6.backend.auth.dto.LoginRequest;
import com.sixlv6.backend.auth.dto.RegisterRequest;
import com.sixlv6.backend.auth.dto.UserDto;
import com.sixlv6.backend.common.ApiResponse;
import com.sixlv6.backend.config.AppProperties;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String REFRESH_COOKIE = "refresh_token";

    private final AuthService authService;
    private final AppProperties properties;

    public AuthController(AuthService authService, AppProperties properties) {
        this.authService = authService;
        this.properties = properties;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResult> register(@Valid @RequestBody RegisterRequest request,
                                            HttpServletResponse response) {
        AuthService.Session session = authService.register(request);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie(session, session.refreshTtl()).toString());
        return ApiResponse.ok(session.result());
    }

    @PostMapping("/login")
    public ApiResponse<AuthResult> login(@Valid @RequestBody LoginRequest request,
                                         HttpServletResponse response) {
        AuthService.Session session = authService.login(request);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie(session, session.refreshTtl()).toString());
        return ApiResponse.ok(session.result());
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResult> refresh(
            @CookieValue(value = REFRESH_COOKIE, required = false) String refreshToken,
            HttpServletResponse response) {
        AuthService.Session session = authService.refresh(refreshToken);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie(session, session.refreshTtl()).toString());
        return ApiResponse.ok(session.result());
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @CookieValue(value = REFRESH_COOKIE, required = false) String refreshToken,
            HttpServletResponse response) {
        authService.logout(refreshToken);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie(null, java.time.Duration.ZERO).toString());
        return ApiResponse.ok();
    }

    @GetMapping("/me")
    public ApiResponse<UserDto> me(@AuthenticationPrincipal AuthUser user) {
        return ApiResponse.ok(authService.me(user.id()));
    }

    /**
     * Refresh Token 只走 Cookie，前端 JS 读不到；Path 限制在 /api/auth，减少被无关请求携带的机会。
     */
    private ResponseCookie refreshCookie(AuthService.Session session, java.time.Duration maxAge) {
        String value = session == null ? "" : session.refreshToken();
        return ResponseCookie.from(REFRESH_COOKIE, value)
                .httpOnly(true)
                .secure(properties.cookie().secure())
                .sameSite(properties.cookie().sameSite())
                .path("/api/auth")
                .maxAge(maxAge)
                .build();
    }
}
