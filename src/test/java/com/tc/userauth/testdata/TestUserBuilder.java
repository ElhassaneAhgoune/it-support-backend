package com.tc.userauth.testdata;

import com.tc.userauth.entity.User;

public class TestUserBuilder {

    public static User createUser() {
        final var user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        return user;
    }

}