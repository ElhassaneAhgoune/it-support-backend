package com.itsupportbackend.mapper;

import com.itsupportbackend.dto.UserProfileDto;
import com.itsupportbackend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserProfileDto toUserProfileDto(final User user) {
        return new UserProfileDto(user.getEmail(), user.getUsername(), user.isEmailVerified());
    }

}