package com.tc.userauth.controller;

import com.tc.userauth.config.JwtConfig;
import com.tc.userauth.config.SecurityConfig;
import com.tc.userauth.service.JpaUserDetailsService;
import com.tc.userauth.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@Import({SecurityConfig.class, JwtConfig.class})
public abstract class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JpaUserDetailsService userDetailsService;

}