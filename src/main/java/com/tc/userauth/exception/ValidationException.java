package com.tc.userauth.exception;

import static com.tc.userauth.exception.ProblemDetailExt.forStatusDetailAndErrors;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

public class ValidationException extends ErrorResponseException {

    public ValidationException(final HttpStatus status, final Map<String, List<String>> errors) {
        super(status, forStatusDetailAndErrors(status, "Request validation failed", errors), null);
    }

}