package com.tc.userauth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tc.userauth.dto.AuthenticationRequestDto;
import com.tc.userauth.dto.AuthenticationResponseDto;
import com.tc.userauth.service.AuthenticationService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends ControllerTest {

    private static final String JWT_TOKEN = "mockJwtToken";

    private static final UUID REFRESH_TOKEN = UUID.randomUUID();

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    void authenticate_validCredentials_returnsToken() throws Exception {
        when(authenticationService.authenticate(any(AuthenticationRequestDto.class))).thenReturn(new AuthenticationResponseDto(JWT_TOKEN, REFRESH_TOKEN));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAuthenticationRequestDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(JWT_TOKEN))
                .andExpect(jsonPath("$.refreshToken").value(REFRESH_TOKEN.toString()));

        verify(authenticationService).authenticate(any(AuthenticationRequestDto.class));
    }

    @Test
    void authenticate_invalidCredentials_returnsUnauthorized() throws Exception {
        when(authenticationService.authenticate(any(AuthenticationRequestDto.class))).thenThrow(new BadCredentialsException("Login failed"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAuthenticationRequestDto())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Login failed"));

        verify(authenticationService).authenticate(any(AuthenticationRequestDto.class));
    }

    @Test
    void refreshToken_validRefreshToken_returnsNewAccessToken() throws Exception {
        final var refreshToken = UUID.randomUUID();
        final var jwtToken = "new-jwt-token";
        final var responseDto = new AuthenticationResponseDto(jwtToken, refreshToken);

        when(authenticationService.refreshToken(eq(refreshToken))).thenReturn(responseDto);

        mockMvc.perform(post("/api/auth/refresh-token")
                        .param("refreshToken", refreshToken.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(jwtToken))
                .andExpect(jsonPath("$.refreshToken").value(refreshToken.toString()));

        verify(authenticationService).refreshToken(eq(refreshToken));
    }

    @Test
    void refreshToken_invalidRefreshToken_throwsAuthenticationException() throws Exception {
        final var invalidRefreshToken = UUID.randomUUID();

        when(authenticationService.refreshToken(eq(invalidRefreshToken)))
                .thenThrow(new BadCredentialsException("Invalid refresh token"));

        mockMvc.perform(post("/api/auth/refresh-token")
                        .param("refreshToken", invalidRefreshToken.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Invalid refresh token"));

        verify(authenticationService).refreshToken(eq(invalidRefreshToken));
    }

    @Test
    void revokeToken_validToken_returnsNoContent() throws Exception {
        final var refreshToken = UUID.randomUUID();

        mockMvc.perform(post("/api/auth/logout")
                        .param("refreshToken", refreshToken.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(authenticationService).revokeRefreshToken(eq(refreshToken));
    }

    private AuthenticationRequestDto newAuthenticationRequestDto() {
        return new AuthenticationRequestDto("testUser", "password123");
    }

}