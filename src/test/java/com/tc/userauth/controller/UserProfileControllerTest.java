package com.tc.userauth.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tc.userauth.entity.User;
import com.tc.userauth.mapper.UserMapper;
import com.tc.userauth.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(UserProfileController.class)
@Import(UserMapper.class)
class UserProfileControllerTest extends ControllerTest {

    private static final String USERNAME = "testUser";
    private static final String EMAIL = "test@example.com";
    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username = USERNAME)
    void shouldReturnUserProfile() throws Exception {
        final var user = new User();

        user.setUsername(USERNAME);
        user.setEmail(EMAIL);

        when(userService.getUserByUsername(USERNAME)).thenReturn(user);

        mockMvc.perform(get("/api/user/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andExpect(jsonPath("$.email").value(EMAIL));

        verify(userService).getUserByUsername(USERNAME);
    }

    @Test
    void shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/user/me")).andExpect(status().isUnauthorized());

        verifyNoInteractions(userService);
    }

}