package com.ticketsystem.service;

import com.ticketsystem.dto.LoginRequest;
import com.ticketsystem.dto.LoginResponse;
import com.ticketsystem.dto.UserDto;
import com.ticketsystem.model.User;

import java.util.List;

public interface UserService {
    LoginResponse login(LoginRequest loginRequest);
    UserDto getUserById(Long id);
    List<UserDto> getAllUsers();
    List<UserDto> getAllITSupportStaff();
    User getCurrentUser();
}


