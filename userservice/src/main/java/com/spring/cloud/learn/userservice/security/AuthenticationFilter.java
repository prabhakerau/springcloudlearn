package com.spring.cloud.learn.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.cloud.learn.userservice.models.LoginRequestModel;
import com.spring.cloud.learn.userservice.services.UserService;
import com.spring.cloud.learn.userservice.shared.UserDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UserService userService;
    private Environment environment;

    @Autowired
    public AuthenticationFilter(UserService userService, Environment environment, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.environment = environment;
        this.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        LoginRequestModel loginRequestModel = null;
        try {
             loginRequestModel = new ObjectMapper().readValue(request.getInputStream(), LoginRequestModel.class);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(
           loginRequestModel.getEmail(), loginRequestModel.getPassword(), new ArrayList<>()
        ));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String userName = ((User)authResult.getPrincipal()).getUsername();
        UserDto userDto = userService.getUserDtoByEmail(userName);
        String token = Jwts.builder().setSubject(userDto.getUserId())
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(environment.getProperty("token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512, environment.getProperty("token.secret"))
                .compact();
        response.addHeader("token", token);
        response.addHeader("userId", userDto.getUserId());
    }
}