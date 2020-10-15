package com.spring.cloud.learn.userservice.services;

import com.spring.cloud.learn.userservice.data.UserEntity;
import com.spring.cloud.learn.userservice.data.UserRepository;
import com.spring.cloud.learn.userservice.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        userRepository.save(userEntity);
        UserDto returnValue = modelMapper.map(userEntity, UserDto.class);
        return returnValue;
    }

    @Override
    public UserDto getUserDtoByEmail(String email) {
        UserEntity userEntity =  userRepository.findByEmail(email);
        if(userEntity == null) {
            throw new UsernameNotFoundException(email);
        }
        return new ModelMapper().map(userEntity, UserDto.class);
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        UserEntity userEntity =  userRepository.findByEmail(userName);
        if(userEntity == null) {
            throw new UsernameNotFoundException(userName);
        }
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true, true, true, true, new ArrayList<>());
    }
}
