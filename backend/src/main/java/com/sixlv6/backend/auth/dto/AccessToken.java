package com.sixlv6.backend.auth.dto;

/** 返回给前端的 Access Token，旧版 token 字段名保持 REST 习惯 */
public record AccessToken(String token, long expiresIn, String tokenType) {

    public static AccessToken bearer(String token, long expiresInSeconds) {
        return new AccessToken(token, expiresInSeconds, "Bearer");
    }
}
