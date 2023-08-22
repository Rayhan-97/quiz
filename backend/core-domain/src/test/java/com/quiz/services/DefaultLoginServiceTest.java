package com.quiz.services;

import com.quiz.configuration.jwt.JwtTokenGenerator;
import com.quiz.services.dtos.UserCredentialsDto;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DefaultLoginServiceTest
{
    private static final org.springframework.security.core.userdetails.User USER = new User("user123", "notImportant", emptyList());
    private static final String EMAIL = "user@email.com";
    private static final String PASSWORD = "password";

    private final AuthenticationManager authenticationManager;
    private final JwtTokenGenerator jwtTokenGenerator;

    private final DefaultLoginService loginService;

    DefaultLoginServiceTest()
    {
        authenticationManager = mock(AuthenticationManager.class);
        jwtTokenGenerator = mock(JwtTokenGenerator.class);

        loginService = new DefaultLoginService(authenticationManager, jwtTokenGenerator);
    }

    @Test
    void givenAuthenticationManagerFindsUser_whenRetrieveLoginToken_thenJwtTokenGenerated()
    {
       when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(USER, "notImportant", emptyList()));

        loginService.retrieveLoginToken(new UserCredentialsDto(EMAIL, PASSWORD));

        verify(jwtTokenGenerator).generate(USER.getUsername());
    }

    @Test
    void givenAuthenticationManagerThrowsException_whenRetrieveLoginToken_thenJwtTokenNotGenerated()
    {
        when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(BadCredentialsException.class);

        assertThatThrownBy(() -> loginService.retrieveLoginToken(new UserCredentialsDto(EMAIL, PASSWORD)))
                .isInstanceOf(BadCredentialsException.class);

        verifyNoInteractions(jwtTokenGenerator);
    }
}