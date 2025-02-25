package com.itsupportbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itsupportbackend.config.JwtConfig;
import com.itsupportbackend.config.SecurityConfig;
import com.itsupportbackend.config.security.BearerTokenAccessDeniedHandler;
import com.itsupportbackend.config.security.BearerTokenAuthenticationEntryPoint;
import com.itsupportbackend.service.JpaUserDetailsService;
import com.itsupportbackend.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@Import({
        SecurityConfig.class,
        JwtConfig.class,
        BearerTokenAuthenticationEntryPoint.class,
        BearerTokenAccessDeniedHandler.class
})
public abstract class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JpaUserDetailsService userDetailsService;

}