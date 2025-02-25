package com.itsupportbackend.testdata;

import static com.itsupportbackend.testdata.TestUserBuilder.userBuilder;
import static java.time.Duration.ofDays;

import com.itsupportbackend.entity.RefreshToken;
import java.time.Instant;
import java.util.UUID;

public class TestRefreshTokenBuilder {

    private final RefreshToken refreshToken;

    private TestRefreshTokenBuilder() {
        refreshToken = new RefreshToken();
        refreshToken.setExpiresAt(Instant.now().plus(ofDays(1)));
    }

    public static TestRefreshTokenBuilder refreshTokenBuilder() {
        return new TestRefreshTokenBuilder();
    }

    public TestRefreshTokenBuilder withRandomId() {
        refreshToken.setId(UUID.randomUUID());
        return this;
    }

    public TestRefreshTokenBuilder withTestUser() {
        refreshToken.setUser(userBuilder().build().getId());
        return this;
    }

    public RefreshToken build() {
        return refreshToken;
    }

}
