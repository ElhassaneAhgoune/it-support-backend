package com.tc.userauth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tc.userauth.entity.User;
import com.tc.userauth.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class JpaUserDetailsServiceTest {

    private static final String USERNAME = "testUser";
    private static final String PASSWORD = "password123";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JpaUserDetailsService jpaUserDetailsService;

    @Mock
    private User user;

    @Test
    void shouldLoadUserByUsernameSuccessfully() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(user.getPassword()).thenReturn(PASSWORD);

        final var userDetails = jpaUserDetailsService.loadUserByUsername(USERNAME);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(USERNAME);
        assertThat(userDetails.getPassword()).isEqualTo(PASSWORD);
        verify(userRepository).findByUsername(USERNAME);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        final var exception = assertThrows(UsernameNotFoundException.class, () -> jpaUserDetailsService.loadUserByUsername(USERNAME));

        assertThat(exception.getMessage()).isEqualTo("User with username [testUser] not found");
        verify(userRepository).findByUsername(USERNAME);
    }
}