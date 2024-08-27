package com.quiz.services;

import com.quiz.configuration.jwt.JwtTokenGenerator;
import com.quiz.services.dtos.UserCredentialsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class DefaultLoginService implements LoginService
{
    private final AuthenticationManager authenticationManager;
    private final JwtTokenGenerator jwtTokenGenerator;

    @Autowired
    public DefaultLoginService(AuthenticationManager authenticationManager, JwtTokenGenerator jwtTokenGenerator)
    {
        this.authenticationManager = authenticationManager;
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    @Override
    public String retrieveLoginToken(UserCredentialsDto userCredentialsDto)
    {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(userCredentialsDto.email(), userCredentialsDto.password());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        UserDetails user = (UserDetails) authentication.getPrincipal();

        return jwtTokenGenerator.generate(user.getUsername());
    }
}
