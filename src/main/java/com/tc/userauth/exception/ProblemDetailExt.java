package com.tc.userauth.exception;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.Nullable;

public class ProblemDetailExt extends ProblemDetail {

    public static ProblemDetail forStatusDetailAndErrors(final HttpStatusCode status, @Nullable final String detail,
                                                         final Map<String, List<String>> errors) {
        final var problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

}
