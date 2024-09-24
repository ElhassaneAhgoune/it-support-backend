package com.tc.userauth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private User user;

    @Test
    void shouldReturnUserWhenUsernameExists() {
        final var username = "testUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        final var user = userService.getUserByUsername(username);

        assertNotNull(user);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void shouldThrowExceptionWhenUsernameNotFound() {
        final var username = "unknownUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        final var exception = assertThrows(ResponseStatusException.class, () -> userService.getUserByUsername(username));

        assertEquals("The user account has been deleted or inactivated", exception.getReason());
        assertEquals(410, exception.getStatusCode().value());
    }
}