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

    private static final String JWT_TOKEN = "mockJwtToken";

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    void authenticate_validCredentials_returnsToken() throws Exception {
        when(authenticationService.authenticate(any(AuthenticationRequestDto.class))).thenReturn(new AuthenticationResponseDto(JWT_TOKEN));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAuthenticationRequestDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(JWT_TOKEN));

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

    private AuthenticationRequestDto newAuthenticationRequestDto() {
        return new AuthenticationRequestDto("testUser", "password123");
    }

}