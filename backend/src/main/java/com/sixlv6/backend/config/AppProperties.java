package com.sixlv6.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * 应用自有配置，绑定 application.yml 里的 app.*。
 * 用 record 做不可变配置，启动期绑定失败会直接报错（fail fast）。
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(Jwt jwt, Cookie cookie) {

    public record Jwt(String secret, Duration accessTtl, Duration refreshTtl) {
    }

    public record Cookie(boolean secure, String sameSite) {
    }
}
