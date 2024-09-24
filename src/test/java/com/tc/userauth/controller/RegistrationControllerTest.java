package com.tc.userauth.controller;

import static com.tc.userauth.testdata.TestUserBuilder.createUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tc.userauth.entity.User;
import com.tc.userauth.exception.ValidationException;
import com.tc.userauth.mapper.UserRegistrationMapper;
import com.tc.userauth.service.UserRegistrationService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

@WebMvcTest(RegistrationController.class)
@Import(UserRegistrationMapper.class)
class RegistrationControllerTest extends ControllerTest {

    private static final String USERNAME = "testUser";
    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "password123";
    private static final String JSON_CONTENT = """
            {
                "username": "%s",
                "email": "%s",
                "password": "%s"
            }
            """.formatted(USERNAME, EMAIL, PASSWORD);
    @MockBean
    private UserRegistrationService userRegistrationService;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        when(userRegistrationService.registerUser(any(User.class))).thenReturn(createUser());

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(JSON_CONTENT))
                .andExpect(status().isOk());

        verify(userRegistrationService).registerUser(any());
    }

    @Test
    void shouldFailToRegisterUser() throws Exception {
        doThrow(new ValidationException(CONFLICT, Map.of("email", "Email is already taken"))).when(userRegistrationService).registerUser(any());

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(JSON_CONTENT))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Request validation failed"));

        verify(userRegistrationService).registerUser(any());
    }
}