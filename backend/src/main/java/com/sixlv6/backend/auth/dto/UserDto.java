package com.sixlv6.backend.auth.dto;

import java.time.Instant;

public record UserDto(Long id, String username, String email, String role, Instant createdAt) {
}
