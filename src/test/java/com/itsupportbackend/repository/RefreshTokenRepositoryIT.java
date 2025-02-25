package com.itsupportbackend.repository;

import static com.itsupportbackend.testdata.TestRefreshTokenBuilder.refreshTokenBuilder;
import static java.time.Duration.ofDays;
import static java.time.Instant.now;
import static org.assertj.core.api.Assertions.assertThat;

import com.itsupportbackend.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RefreshTokenRepositoryIT extends JpaTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByIdAndExpiresAtAfter_existingToken_returnsToken() {
        final var testToken = refreshTokenBuilder()
                .withTestUser()
                .build();
        User user = new User();
        userRepository.save(user);
        refreshTokenRepository.save(testToken);

        final var foundToken = refreshTokenRepository.findByIdAndExpiresAtAfter(testToken.getId(), now());

        assertThat(foundToken).isPresent();
        assertThat(foundToken.get().getId()).isEqualTo(testToken.getId());
    }

    @Test
    void findByIdAndExpiresAtAfter_expiredToken_returnsEmpty() {
        final var testToken = refreshTokenBuilder()
                .withTestUser()
                .build();
        User user = new User();
        userRepository.save(user);
        refreshTokenRepository.save(testToken);

        final var foundToken = refreshTokenRepository.findByIdAndExpiresAtAfter(testToken.getId(), now().plus(ofDays(2)));

        assertThat(foundToken).isNotPresent();
    }

}