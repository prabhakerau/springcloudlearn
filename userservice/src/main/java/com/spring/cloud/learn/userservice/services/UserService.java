package com.spring.cloud.learn.userservice.services;

import com.spring.cloud.learn.userservice.shared.UserDto;

public interface UserService {
    UserDto createUser(UserDto userDto);
}
