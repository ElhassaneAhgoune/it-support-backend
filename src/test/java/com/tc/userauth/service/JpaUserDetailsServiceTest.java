package com.tc.userauth.service;

import static com.tc.userauth.testdata.TestUserBuilder.userBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tc.userauth.exception.EmailVerificationException;
import com.tc.userauth.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JpaUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private JpaUserDetailsService jpaUserDetailsService;

    @BeforeEach
    void setUp() {
        jpaUserDetailsService = new JpaUserDetailsService(false, userRepository);
    }

    @Test
    void loadUserByUsername_existingUser_verificationNotRequired_returnsUserDetails() {
        final var user = userBuilder().build();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        final var userDetails = jpaUserDetailsService.loadUserByUsername(user.getUsername());

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(user.getUsername());
        assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());
        verify(userRepository).findByUsername(user.getUsername());
    }

    @Test
    void loadUserByUsername_userNotFound_throwsUsernameNotFoundException() {
        final var user = userBuilder().build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        final var exception = assertThrows(UsernameNotFoundException.class, () -> jpaUserDetailsService.loadUserByUsername(user.getUsername()));

        assertThat(exception.getMessage()).isEqualTo("User with username [testUser] not found");
        verify(userRepository).findByUsername(anyString());
    }

    @Test
    void loadUserByUsername_userNotVerified_verificationRequired_throwsEmailVerificationException() {
        ReflectionTestUtils.setField(jpaUserDetailsService, "emailVerificationRequired", true);

        final var user = userBuilder().build();
        when(userRepository.findByUsername(eq(user.getUsername()))).thenReturn(Optional.of(user));

        assertThrows(EmailVerificationException.class, () -> jpaUserDetailsService.loadUserByUsername(user.getUsername()));

        verify(userRepository).findByUsername(eq(user.getUsername()));
    }

    @Test
    void loadUserByUsername_userVerified_verificationRequired_returnsUserDetails() {
        ReflectionTestUtils.setField(jpaUserDetailsService, "emailVerificationRequired", true);

        final var user = userBuilder().withVerifiedEmail().build();

        when(userRepository.findByUsername(eq(user.getUsername()))).thenReturn(Optional.of(user));

        final var userDetails = jpaUserDetailsService.loadUserByUsername(user.getUsername());

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(user.getUsername());
        assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());
        verify(userRepository).findByUsername(eq(user.getUsername()));
    }

    @Test
    void loadUserByUsername_userInactive_throwsException() {
        ReflectionTestUtils.setField(jpaUserDetailsService, "emailVerificationRequired", true);

        final var user = userBuilder().build();

        when(userRepository.findByUsername(eq(user.getUsername()))).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> jpaUserDetailsService.loadUserByUsername(user.getUsername()))
                .isInstanceOf(EmailVerificationException.class)
                .hasMessage("Your email is not verified. Please verify your email before logging in");
    }

}