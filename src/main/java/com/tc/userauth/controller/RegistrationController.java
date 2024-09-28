package com.tc.userauth.controller;

import com.tc.userauth.dto.RegistrationRequestDto;
import com.tc.userauth.dto.RegistrationResponseDto;
import com.tc.userauth.mapper.UserRegistrationMapper;
import com.tc.userauth.service.EmailVerificationService;
import com.tc.userauth.service.UserRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RegistrationController {

    @Value("${email-verification.required}")
    private final boolean emailVerificationRequired;

    private final UserRegistrationService userRegistrationService;

    private final EmailVerificationService emailVerificationService;

    private final UserRegistrationMapper userRegistrationMapper;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDto> registerUser(@Valid @RequestBody final RegistrationRequestDto registrationDTO) {
        final var registeredUser = userRegistrationService.registerUser(userRegistrationMapper.toEntity(registrationDTO));

        if (emailVerificationRequired) {
            emailVerificationService.sendVerificationToken(registeredUser.getId(), registeredUser.getEmail());
        }
        return ResponseEntity.ok(userRegistrationMapper.toRegistrationResponseDto(registeredUser, emailVerificationRequired));
    }

}