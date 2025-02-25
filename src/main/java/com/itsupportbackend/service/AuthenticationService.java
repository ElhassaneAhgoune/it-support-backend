package com.itsupportbackend.service;

import static java.time.Duration.between;

import com.itsupportbackend.entity.RefreshToken;
import com.itsupportbackend.entity.User;
import com.itsupportbackend.model.AuthTokens;
import com.itsupportbackend.repository.RefreshTokenRepository;
import com.itsupportbackend.repository.UserRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Value("${jwt.refresh-token-ttl}")
    private final Duration refreshTokenTtl;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    public AuthTokens authenticate(final String username, final String password) {
        final var authToken = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        final var authentication = authenticationManager.authenticate(authToken);

        final var user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User with username [%s] not found".formatted(username)));

        return authenticate(user);
    }

    public AuthTokens authenticate(final User user) {
        final var accessToken = jwtService.generateToken(user.getUsername() , user.getRole().getRoleName());

        final var refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUser(user.getId());
        refreshTokenEntity.setExpiresAt(Instant.now().plus(refreshTokenTtl));
        refreshTokenRepository.save(refreshTokenEntity);

        return new AuthTokens(accessToken, refreshTokenEntity.getId().toString(), between(Instant.now(), refreshTokenEntity.getExpiresAt()));
    }

    public AuthTokens refreshToken(final String refreshToken) {
        final var refreshTokenEntity = refreshTokenRepository.findByIdAndExpiresAtAfter(validateRefreshTokenFormat(refreshToken), Instant.now())
                .orElseThrow(() -> new BadCredentialsException("Invalid or expired refresh token"));

        User user = userRepository.findById(refreshTokenEntity.getUser())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        final var newAccessToken = jwtService.generateToken(user.getUsername() , user.getRole().getRoleName());

        return new AuthTokens(newAccessToken, refreshToken, between(Instant.now(), refreshTokenEntity.getExpiresAt()));
    }

    public void revokeRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteById(validateRefreshTokenFormat(refreshToken));
    }

    private UUID validateRefreshTokenFormat(final String refreshToken) {
        try {
            return UUID.fromString(refreshToken);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }
    }
}