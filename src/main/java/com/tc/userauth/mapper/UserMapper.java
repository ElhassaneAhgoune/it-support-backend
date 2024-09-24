package com.tc.userauth.mapper;

import com.tc.userauth.dto.UserProfileDto;
import com.tc.userauth.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserProfileDto toUserProfileDto(final User user) {
        return new UserProfileDto(user.getEmail(), user.getUsername());
    }

}