package com.tc.userauth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tc.userauth.config.JwtConfig;
import com.tc.userauth.config.SecurityConfig;
import com.tc.userauth.config.security.BearerTokenAccessDeniedHandler;
import com.tc.userauth.config.security.BearerTokenAuthenticationEntryPoint;
import com.tc.userauth.service.JpaUserDetailsService;
import com.tc.userauth.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@Import({SecurityConfig.class, JwtConfig.class, BearerTokenAuthenticationEntryPoint.class, BearerTokenAccessDeniedHandler.class})
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