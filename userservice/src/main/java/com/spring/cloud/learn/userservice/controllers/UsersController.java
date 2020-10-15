package com.spring.cloud.learn.userservice.controllers;

import com.spring.cloud.learn.userservice.models.CreateUserRequestModel;
import com.spring.cloud.learn.userservice.models.CreateUserResponseModel;
import com.spring.cloud.learn.userservice.services.UserService;
import com.spring.cloud.learn.userservice.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UsersController {



    @Autowired
    private UserService userService;

    @GetMapping
    public  String status() {
        return "hello world.! .";
    }

    @PostMapping
    public ResponseEntity<CreateUserResponseModel> createUser(@Valid @RequestBody CreateUserRequestModel createUserRequestModel) {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = modelMapper.map(createUserRequestModel, UserDto.class);
        UserDto createdUser = userService.createUser(userDto);
        CreateUserResponseModel response = modelMapper.map(createdUser, CreateUserResponseModel.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
