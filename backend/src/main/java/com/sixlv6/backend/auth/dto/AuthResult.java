package com.sixlv6.backend.auth.dto;

public record AuthResult(AccessToken accessToken, UserDto user) {
}
