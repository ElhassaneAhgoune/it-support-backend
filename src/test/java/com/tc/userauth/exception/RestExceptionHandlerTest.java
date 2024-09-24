package com.tc.userauth.exception;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

@ExtendWith(MockitoExtension.class)
class RestExceptionHandlerTest {

    @InjectMocks
    private RestExceptionHandler restExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Test
    void shouldHandleMethodArgumentNotValidException() {
        final var fieldErrors = List.of(
                new FieldError("objectName", "email", "Invalid email"),
                new FieldError("objectName", "username", "Username already taken")
        );

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        final var response = restExceptionHandler.handleMethodArgumentNotValid(
                methodArgumentNotValidException, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest
        );

        final var problemDetail = (ProblemDetail) requireNonNull(response).getBody();
        assertNotNull(problemDetail);
        assertEquals("Request validation failed.", problemDetail.getDetail());
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertNotNull(problemDetail.getProperties());
        assertNotNull(problemDetail.getProperties().get("errors"));
    }

    @Test
    void shouldHandleAuthenticationException() {
        final var authenticationException = mock(AuthenticationException.class);
        when(authenticationException.getMessage()).thenReturn("Unauthorized access.");

        final var response = restExceptionHandler.handleAuthenticationException(authenticationException);

        final var problemDetail = response.getBody();
        assertNotNull(problemDetail);
        assertEquals("Unauthorized access.", problemDetail.getDetail());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), problemDetail.getStatus());
    }

    @Test
    void shouldHandleConstraintViolationException() {
        final var constraintViolationException = mock(ConstraintViolationException.class);

        final var response = restExceptionHandler.handleConstraintViolationException(
                constraintViolationException, webRequest
        );

        final var problemDetail = response.getBody();
        assertNotNull(problemDetail);
        assertEquals("Error while processing the request. Please try again.", problemDetail.getDetail());
        assertEquals(HttpStatus.CONFLICT.value(), problemDetail.getStatus());
    }

    @Test
    void shouldHandleGenericException() {
        final var genericException = new Exception("Something went wrong");

        final var response = restExceptionHandler.handleGenericException(genericException);

        final var problemDetail = response.getBody();
        assertNotNull(problemDetail);
        assertEquals("An unexpected error occurred.", problemDetail.getDetail());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), problemDetail.getStatus());
    }
}