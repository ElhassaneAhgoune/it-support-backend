package com.tc.userauth.controller;

import static com.tc.userauth.testdata.TestUserBuilder.userBuilder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tc.userauth.dto.RegistrationRequestDto;
import com.tc.userauth.entity.User;
import com.tc.userauth.exception.ValidationException;
import com.tc.userauth.mapper.UserRegistrationMapper;
import com.tc.userauth.service.EmailVerificationService;
import com.tc.userauth.service.UserRegistrationService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

@WebMvcTest(RegistrationController.class)
@Import(UserRegistrationMapper.class)
class RegistrationControllerTest extends ControllerTest {

    private static final String USERNAME = "testUser";
    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "password123";

    @MockBean
    private EmailVerificationService emailVerificationService;

    @MockBean
    private UserRegistrationService userRegistrationService;

    @Autowired
    private RegistrationController registrationController;

    @Test
    void registerUser_validRequest_verificationRequired_returnsOk() throws Exception {
        ReflectionTestUtils.setField(registrationController, "emailVerificationRequired", true);

        final var user = userBuilder().build();

        when(userRegistrationService.registerUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegistrationRequestDto(USERNAME, EMAIL, PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailVerificationRequired").value(true));

        verify(userRegistrationService).registerUser(any());
        verify(emailVerificationService).sendVerificationToken(eq(user.getId()), eq(user.getEmail()));
    }

    @Test
    void registerUser_validRequest_noVerificationRequired_returnsOk() throws Exception {
        ReflectionTestUtils.setField(registrationController, "emailVerificationRequired", false);

        when(userRegistrationService.registerUser(any(User.class))).thenReturn(userBuilder().build());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegistrationRequestDto(USERNAME, EMAIL, PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailVerificationRequired").value(false));

        verify(userRegistrationService).registerUser(any());
        verifyNoInteractions(emailVerificationService);
    }

    @Test
    void registerUser_emailOrUsernameExists_returnsConflict() throws Exception {
        doThrow(new ValidationException(CONFLICT, Map.of("email", "Email is already taken")))
                .when(userRegistrationService)
                .registerUser(any());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegistrationRequestDto(USERNAME, EMAIL, PASSWORD))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Request validation failed"));

        verify(userRegistrationService).registerUser(any());
        verifyNoInteractions(emailVerificationService);
    }

    @Test
    void registerUser_invalidEmailFormat_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegistrationRequestDto(USERNAME, "invalid-email", PASSWORD))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").value("Please provide a valid email address"));
    }

    @Test
    void registerUser_invalidUsernameLength_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegistrationRequestDto("tu", EMAIL, PASSWORD))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.username").value("Username must be between 3 and 20 characters"));
    }

    private RegistrationRequestDto newRegistrationRequestDto(String username, String email, String password) {
        return new RegistrationRequestDto(username, email, password);
    }

}