package com.tc.userauth.service;

import static org.springframework.http.HttpStatus.CONFLICT;

import com.tc.userauth.entity.User;
import com.tc.userauth.exception.ValidationException;
import com.tc.userauth.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(User user) {
        final var errors = new HashMap<String, List<String>>();

        if (userRepository.existsByEmail(user.getEmail())) {
            errors.put("email", List.of("Email [%s] is already taken".formatted(user.getEmail())));
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            errors.put("username", List.of("Username [%s] is already taken".formatted(user.getUsername())));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(CONFLICT, errors);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

}