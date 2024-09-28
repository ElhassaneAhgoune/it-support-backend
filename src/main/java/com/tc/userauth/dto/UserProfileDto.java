package com.tc.userauth.dto;

public record UserProfileDto(String email, String username, boolean emailVerified) {
}
