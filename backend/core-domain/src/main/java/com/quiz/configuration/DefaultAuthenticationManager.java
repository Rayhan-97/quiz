package com.quiz.configuration;

import com.quiz.services.PasswordHashGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class DefaultAuthenticationManager implements AuthenticationManager
{
    private final UserDetailsService userDetailsService;
    private final PasswordHashGenerator passwordHashGenerator;

    @Autowired
    public DefaultAuthenticationManager(UserDetailsService userDetailsService, PasswordHashGenerator passwordHashGenerator)
    {
        this.userDetailsService = userDetailsService;
        this.passwordHashGenerator = passwordHashGenerator;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails userDetails;
        try
        {
            userDetails = userDetailsService.loadUserByUsername(email);
        }
        catch (UsernameNotFoundException exception)
        {
            throw new BadCredentialsException("User not found", exception);
        }

        String encodedPassword = userDetails.getPassword();
        if (!passwordHashGenerator.matches(password, encodedPassword))
        {
            throw new BadCredentialsException("Invalid credentials");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, encodedPassword, userDetails.getAuthorities());
    }
}
