package com.tc.userauth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tc.userauth.dto.AuthenticationRequestDto;
import com.tc.userauth.dto.AuthenticationResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void authenticate_validRequest_returnsJwtToken() {
        final var request = new AuthenticationRequestDto("user", "password");
        final var authToken = new UsernamePasswordAuthenticationToken(request.username(), request.password());

        final var authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(authToken)).thenReturn(authentication);
        when(jwtService.generateToken(request.username())).thenReturn("mocked-jwt-token");

        final var response = authenticationService.authenticate(request);

        assertThat(response).isEqualTo(new AuthenticationResponseDto("mocked-jwt-token"));
        verify(authenticationManager).authenticate(authToken);
        verify(jwtService).generateToken(request.username());
    }
}