package com.itsupportbackend.service;

import static com.itsupportbackend.testdata.TestRefreshTokenBuilder.refreshTokenBuilder;
import static com.itsupportbackend.testdata.TestUserBuilder.userBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.itsupportbackend.entity.RefreshToken;
import com.itsupportbackend.entity.Role;
import com.itsupportbackend.entity.User;
import com.itsupportbackend.repository.RefreshTokenRepository;
import com.itsupportbackend.repository.UserRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authenticationService, "refreshTokenTtl", Duration.ofDays(1));
    }

    @Test
    void authenticate_validRequest_returnsJwtToken() {
        final var username = "testUser";
        final var password = "password";
        final var authToken = new UsernamePasswordAuthenticationToken(username, password);
        final var refreshTokenValue = UUID.randomUUID();

        final var authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(authToken)).thenReturn(authentication);
        when(jwtService.generateToken(username, Role.EMPLOYEE.getRoleName())).thenReturn("mocked-jwt-token");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userBuilder().build()));

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(parameters -> {
            var refreshToken = (RefreshToken) parameters.getArgument(0);
            refreshToken.setId(refreshTokenValue);
            return refreshToken;
        });

        final var response = authenticationService.authenticate(username, password);

        assertThat(response.accessToken()).isEqualTo("mocked-jwt-token");
        assertThat(response.refreshToken()).isEqualTo(refreshTokenValue.toString());

        verify(authenticationManager).authenticate(authToken);
        verify(jwtService).generateToken(username, Role.EMPLOYEE.getRoleName());
    }

    @Test
    void refreshToken_validToken_returnsNewAccessToken() {
        final var refreshToken = refreshTokenBuilder().withRandomId().withTestUser().build();

        when(refreshTokenRepository.findByIdAndExpiresAtAfter(eq(refreshToken.getId()), any(Instant.class)))
                .thenReturn(Optional.of(refreshToken));
        User user = userBuilder().build();
        when(userRepository.findById(refreshToken.getUser())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user.getUsername(),user.getRole().getRoleName())).thenReturn("new-jwt-token");

        final var response = authenticationService.refreshToken(refreshToken.getId().toString());

        assertThat(response.accessToken()).isEqualTo("new-jwt-token");
        assertThat(response.refreshToken()).isEqualTo(refreshToken.getId().toString());

        verify(refreshTokenRepository).findByIdAndExpiresAtAfter(eq(refreshToken.getId()), any(Instant.class));
        verify(jwtService).generateToken(user.getUsername(),user.getRole().getRoleName());
    }

    @Test
    void refreshToken_invalidToken_throwsAuthenticationException() {
        final var invalidRefreshToken = UUID.randomUUID();

        when(refreshTokenRepository.findByIdAndExpiresAtAfter(eq(invalidRefreshToken), any(Instant.class)))
                .thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> authenticationService.refreshToken(invalidRefreshToken.toString()));

        verify(refreshTokenRepository).findByIdAndExpiresAtAfter(eq(invalidRefreshToken), any(Instant.class));
        verifyNoInteractions(jwtService);
    }

    @Test
    void revokeRefreshToken_validToken_deletesToken() {
        final var refreshToken = UUID.randomUUID();

        authenticationService.revokeRefreshToken(refreshToken.toString());

        verify(refreshTokenRepository).deleteById(refreshToken);
    }

}