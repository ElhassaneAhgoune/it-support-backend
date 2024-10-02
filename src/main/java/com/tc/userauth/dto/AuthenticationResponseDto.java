package com.tc.userauth.dto;

import java.util.UUID;

public record AuthenticationResponseDto(String accessToken, UUID refreshToken) {
}