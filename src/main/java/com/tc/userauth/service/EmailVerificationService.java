package com.tc.userauth.service;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.GONE;

import com.tc.userauth.entity.User;
import com.tc.userauth.repository.UserRepository;
import com.tc.userauth.util.CryptoUtil;
import jakarta.transaction.Transactional;
import java.net.URLEncoder;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    @Value("${email-verification.base-url}")
    private final String baseUrl;

    private final OtpService otpService;

    private final CryptoUtil cryptoUtil;

    private final UserRepository userRepository;

    private final JavaMailSender mailSender;

    @Async
    public void sendVerificationToken(UUID userId, String email) {
        final var token = otpService.generateAndStoreOtp(userId);

        final var emailVerificationUrl = baseUrl.formatted(URLEncoder.encode(cryptoUtil.encrypt(userId.toString()), UTF_8), token);
        final var emailText = "Click the link to verify the email: " + emailVerificationUrl;

        final var message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Email Verification");
        message.setFrom("System");
        message.setText(emailText);

        mailSender.send(message);
    }

    public void resendVerificationToken(String email) {
        userRepository.findByEmail(email).filter(user -> !user.isEmailVerified())
                .ifPresentOrElse(user -> sendVerificationToken(user.getId(), user.getEmail()),
                        () -> log.warn("Attempt to resend verification token for non existing or already validated email: [{}]", email));
    }

    @Transactional
    public User verifyEmail(String encryptedUserId, String token) {
        final var userId = UUID.fromString(cryptoUtil.decrypt(encryptedUserId));

        if (!otpService.isOtpValid(userId, token)) {
            throw new ResponseStatusException(BAD_REQUEST, "Token invalid or expired");
        }
        otpService.deleteOtp(userId);

        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(GONE, "The user account has been deleted or inactivated"));

        if (user.isEmailVerified()) {
            throw new ResponseStatusException(BAD_REQUEST, "Email is already verified");
        }

        user.setEmailVerified(true);

        return user;
    }

}