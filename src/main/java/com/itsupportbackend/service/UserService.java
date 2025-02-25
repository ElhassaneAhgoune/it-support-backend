package com.itsupportbackend.service;

import static com.itsupportbackend.exception.ErrorType.ACCOUNT_UNAVAILABLE;
import static com.itsupportbackend.exception.ProblemDetailBuilder.forStatusAndDetail;
import static org.springframework.http.HttpStatus.GONE;

import com.itsupportbackend.entity.User;
import com.itsupportbackend.exception.RestErrorResponseException;
import com.itsupportbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserByUsername(final String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new RestErrorResponseException(forStatusAndDetail(GONE, "The user account has been deleted or inactivated")
                        .withErrorType(ACCOUNT_UNAVAILABLE)
                        .build()
                )
        );
    }
}