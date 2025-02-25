package com.itsupportbackend.mapper;

import com.itsupportbackend.dto.RegistrationRequestDto;
import com.itsupportbackend.dto.RegistrationResponseDto;
import com.itsupportbackend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationMapper {

    public User toEntity(final RegistrationRequestDto registrationRequestDto) {
        final var user = new User();

        user.setEmail(registrationRequestDto.email());
        user.setUsername(registrationRequestDto.username());
        user.setPassword(registrationRequestDto.password());
        user.setRole(registrationRequestDto.role());
        user.setEmailVerified(true);

        return user;
    }

    public RegistrationResponseDto toResponseDto(final User user) {
        return new RegistrationResponseDto(user.getEmail(), user.getUsername());
    }

}