package com.tc.userauth.service;

import static com.tc.userauth.testdata.TestUserBuilder.userBuilder;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tc.userauth.entity.User;
import com.tc.userauth.repository.UserRepository;
import com.tc.userauth.util.CryptoUtil;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private OtpService otpService;

    @Mock
    private CryptoUtil cryptoUtil;

    @InjectMocks
    private EmailVerificationService emailVerificationService;

    @BeforeEach
    void setUp() {
        emailVerificationService = new EmailVerificationService(
                "http://url?uid=%s&t=%s", otpService, cryptoUtil, userRepository, mailSender);
    }

    @Test
    void sendVerificationToken_validUser_sendsToken() {
        final var user = userBuilder().withId().build();
        final var token = UUID.randomUUID().toString();

        when(cryptoUtil.encrypt(anyString())).then(a -> a.getArgument(0));
        when(otpService.generateAndStoreOtp(eq(user.getId()))).thenReturn(token);

        emailVerificationService.sendVerificationToken(user.getId(), user.getEmail());

        verify(otpService).generateAndStoreOtp(eq(user.getId()));
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void resendVerificationToken_unverifiedEmail_resendsToken() {
        final var user = userBuilder().withId().build();

        when(cryptoUtil.encrypt(anyString())).then(a -> a.getArgument(0));
        when(otpService.generateAndStoreOtp(eq(user.getId()))).thenReturn("token");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(of(user));

        emailVerificationService.resendVerificationToken(user.getEmail());

        verify(userRepository).findByEmail(user.getEmail());
        verify(mailSender).send(any(SimpleMailMessage.class));
        verify(otpService).generateAndStoreOtp(eq(user.getId()));
    }

    @Test
    void verifyEmail_validToken_verifiesEmail() {
        final var token = UUID.randomUUID().toString();
        final var user = userBuilder().withId().build();

        when(cryptoUtil.decrypt(anyString())).then(a -> a.getArgument(0));
        when(otpService.isOtpValid(eq(user.getId()), anyString())).thenReturn(true);
        when(userRepository.findById(user.getId())).thenReturn(of(user));

        emailVerificationService.verifyEmail(user.getId().toString(), token);

        assertTrue(user.isEmailVerified());
        verify(userRepository).findById(user.getId());
        verify(otpService).deleteOtp(eq(user.getId()));
    }

    @Test
    void verifyEmail_invalidToken_throwsValidationException() {
        final var token = UUID.randomUUID().toString();

        when(cryptoUtil.decrypt(anyString())).thenReturn(UUID.randomUUID().toString());
        when(otpService.isOtpValid(any(UUID.class), anyString())).thenReturn(false);

        final var exception = assertThrows(ResponseStatusException.class, () -> emailVerificationService.verifyEmail("invalid-user", token));
        assertThat(exception.getReason()).isEqualTo("Token invalid or expired");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void verifyEmail_alreadyVerified_throwsValidationException() {
        final var token = UUID.randomUUID().toString();

        final var user = userBuilder().withId().withVerifiedEmail().build();

        when(cryptoUtil.decrypt(anyString())).then(a -> a.getArgument(0));
        when(otpService.isOtpValid(eq(user.getId()), anyString())).thenReturn(true);
        when(userRepository.findById(user.getId())).thenReturn(of(user));

        final var exception = assertThrows(ResponseStatusException.class, () -> emailVerificationService.verifyEmail(user.getId().toString(), token));
        assertThat(exception.getReason()).isEqualTo("Email is already verified");

        verify(userRepository, never()).save(any(User.class));
    }

}