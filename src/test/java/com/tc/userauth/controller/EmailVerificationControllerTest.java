package com.tc.userauth.controller;

import static com.tc.userauth.testdata.TestUserBuilder.userBuilder;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tc.userauth.mapper.UserMapper;
import com.tc.userauth.service.EmailVerificationService;
import com.tc.userauth.util.CryptoUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(EmailVerificationController.class)
@Import(UserMapper.class)
class EmailVerificationControllerTest extends ControllerTest {

    @MockBean
    private EmailVerificationService emailVerificationService;

    @MockBean
    private CryptoUtil cryptoUtil;

    @Test
    void resendVerificationLink_validEmail_returnsNoContent() throws Exception {
        doNothing().when(emailVerificationService).resendVerificationToken(anyString());

        mockMvc.perform(post("/api/auth/email/resend-verification").param("email", "test@example.com"))
                .andExpect(status().isNoContent());

        verify(emailVerificationService).resendVerificationToken("test@example.com");
    }

    @Test
    void verifyEmail_validToken_returnsVerifiedUser() throws Exception {
        when(cryptoUtil.decrypt(anyString())).thenReturn("user-id");
        when(emailVerificationService.verifyEmail(anyString(), anyString())).thenReturn(userBuilder().withVerifiedEmail().build());

        mockMvc.perform(get("/api/auth/email/verify").param("uid", "user-id").param("t", "token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailVerified").value(true));

        verify(emailVerificationService).verifyEmail(eq("user-id"), eq("token"));
    }

    @Test
    void verifyEmail_invalidToken_returnsBadRequest() throws Exception {
        when(cryptoUtil.decrypt(anyString())).thenReturn("user-id");
        when(emailVerificationService.verifyEmail(anyString(), anyString())).thenThrow(new ResponseStatusException(BAD_REQUEST, "Invalid token"));

        mockMvc.perform(get("/api/auth/email/verify").param("uid", "user-id").param("t", "invalid-token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid token"));
    }

    @Test
    void resendVerificationLink_emailNotFound_returnsNotFound() throws Exception {
        doThrow(new ResponseStatusException(NOT_FOUND, "Email not found"))
                .when(emailVerificationService).resendVerificationToken("nonexistent@example.com");

        mockMvc.perform(post("/api/auth/email/resend-verification").param("email", "nonexistent@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Email not found"));
    }

}