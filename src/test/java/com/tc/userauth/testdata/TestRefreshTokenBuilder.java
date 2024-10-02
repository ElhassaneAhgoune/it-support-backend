package com.tc.userauth.testdata;

import static com.tc.userauth.testdata.TestUserBuilder.userBuilder;

import com.tc.userauth.entity.RefreshToken;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class TestRefreshTokenBuilder {

    private final RefreshToken refreshToken;

    private TestRefreshTokenBuilder() {
        refreshToken = new RefreshToken();
        refreshToken.setExpiresAt(Instant.now().plus(Duration.ofDays(1)));
    }

    public static TestRefreshTokenBuilder refreshTokenBuilder() {
        return new TestRefreshTokenBuilder();
    }

    public TestRefreshTokenBuilder withId() {
        refreshToken.setId(UUID.randomUUID());
        return this;
    }

    public TestRefreshTokenBuilder withTestUser() {
        refreshToken.setUser(userBuilder().build());
        return this;
    }

    public RefreshToken build() {
        return refreshToken;
    }

}
