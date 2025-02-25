package com.itsupportbackend.service;

import static com.itsupportbackend.exception.ErrorType.EMAIL_VERIFICATION_REQUIRED;
import static com.itsupportbackend.exception.ProblemDetailBuilder.forStatusAndDetail;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.itsupportbackend.exception.RestErrorResponseException;
import com.itsupportbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) {
        return userRepository.findByUsername(username).map(user -> {
            if (!user.isEmailVerified()) {
                throw new RestErrorResponseException(forStatusAndDetail(UNAUTHORIZED, "Email verification required")
                        .withProperty("email", user.getEmail())
                        .withErrorType(EMAIL_VERIFICATION_REQUIRED)
                        .build()
                );
            }
            return User.builder()
                    .username(username)
                    .password(user.getPassword())
                    .build();
        }).orElseThrow(() -> new UsernameNotFoundException("User with username [%s] not found".formatted(username)));
    }

}