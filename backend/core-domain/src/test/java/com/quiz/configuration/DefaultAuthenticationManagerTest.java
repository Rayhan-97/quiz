package com.quiz.configuration;

import com.quiz.services.PasswordHashGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultAuthenticationManagerTest
{
    private static final String EMAIL = "user@email.com";
    private static final String PASSWORD = "password";
    private static final String PASSWORD_HASH = "passwordHash";

    private final UserDetailsService userDetailsService;
    private final PasswordHashGenerator passwordHashGenerator;

    private final AuthenticationManager authenticationManager;

    DefaultAuthenticationManagerTest()
    {
        userDetailsService = mock(UserDetailsService.class);
        passwordHashGenerator = mock(PasswordHashGenerator.class);

        authenticationManager = new DefaultAuthenticationManager(userDetailsService, passwordHashGenerator);

        when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(new User(EMAIL, PASSWORD_HASH, emptyList()));
    }

    @Test
    void givenUserAndMatchingPassword_whenAuthenticate_thenReturnAuthenticationToken()
    {
        when(passwordHashGenerator.matches(PASSWORD, PASSWORD_HASH)).thenReturn(true);

        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(EMAIL, PASSWORD));

        assertThat(authenticate.getName()).isEqualTo(EMAIL);
        assertThat(authenticate.getCredentials().toString()).isEqualTo(PASSWORD_HASH);
    }

    @Test
    void givenUserAndWrongPassword_whenAuthenticate_thenThrowBadCredentialsException()
    {
        when(passwordHashGenerator.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(EMAIL, "wrongPassword")))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void givenUserDoesNotExist_whenAuthenticate_thenThrowBadCredentialsException()
    {
        when(userDetailsService.loadUserByUsername(any())).thenThrow(UsernameNotFoundException.class);
        when(passwordHashGenerator.matches(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("WRONG_EMAIL", PASSWORD)))
                .isInstanceOf(BadCredentialsException.class);
    }
}