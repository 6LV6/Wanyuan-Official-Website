package com.sixlv6.backend.auth;

/** 放进 SecurityContext 的当前用户，不含密码等敏感字段 */
public record AuthUser(Long id, String username, String role) {
}
