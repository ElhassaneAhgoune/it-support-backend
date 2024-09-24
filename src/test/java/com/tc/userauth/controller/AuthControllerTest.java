package com.tc.userauth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tc.userauth.dto.AuthenticationRequestDto;
import com.tc.userauth.dto.AuthenticationResponseDto;
import com.tc.userauth.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends ControllerTest {

    private static final String USERNAME = "testUser";
    private static final String PASSWORD = "password123";
    private static final String JWT_TOKEN = "mockJwtToken";
    private static final String JSON_CONTENT = """
            {
                "username": "%s",
                "password": "%s"
            }
            """.formatted(USERNAME, PASSWORD);
    @MockBean
    private AuthenticationService authenticationService;

    @Test
    void shouldLoginSuccessfully() throws Exception {
        when(authenticationService.authenticate(any(AuthenticationRequestDto.class))).thenReturn(new AuthenticationResponseDto(JWT_TOKEN));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CONTENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(JWT_TOKEN));

        verify(authenticationService).authenticate(any(AuthenticationRequestDto.class));
    }

    @Test
    void shouldFailLoginWithIncorrectCredentials() throws Exception {
        when(authenticationService.authenticate(any(AuthenticationRequestDto.class))).thenThrow(new BadCredentialsException("Login failed"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CONTENT))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Login failed"));

        verify(authenticationService).authenticate(any(AuthenticationRequestDto.class));
    }
}