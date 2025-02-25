package com.itsupportbackend.controller;

import com.itsupportbackend.dto.RegistrationRequestDto;
import com.itsupportbackend.dto.RegistrationResponseDto;
import com.itsupportbackend.mapper.UserRegistrationMapper;
import com.itsupportbackend.service.EmailVerificationService;
import com.itsupportbackend.service.UserRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserRegistrationService userRegistrationService;

    private final EmailVerificationService emailVerificationService;

    private final UserRegistrationMapper userRegistrationMapper;

    @PostMapping("/sign-up")
    public ResponseEntity<RegistrationResponseDto> registerUser(@Valid @RequestBody final RegistrationRequestDto registrationDTO) {
        final var registeredUser = userRegistrationService.registerUser(userRegistrationMapper.toEntity(registrationDTO));

        emailVerificationService.sendEmailVerificationOtp(registeredUser.getId(), registeredUser.getEmail());

        return ResponseEntity.ok(userRegistrationMapper.toResponseDto(registeredUser));
    }

}