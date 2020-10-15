package com.spring.cloud.learn.userservice.services;

import com.spring.cloud.learn.userservice.shared.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);

    UserDto getUserDtoByEmail(String email);
}
