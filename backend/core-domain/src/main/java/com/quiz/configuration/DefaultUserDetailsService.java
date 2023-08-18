package com.quiz.configuration;

import com.quiz.core.entities.User;
import com.quiz.core.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class DefaultUserDetailsService implements UserDetailsService
{
    private final UserRepository userRepository;

    @Autowired
    public DefaultUserDetailsService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException
    {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty())
        {
            throw new UsernameNotFoundException("User with email %s not found".formatted(email));
        }

        User user = optionalUser.get();

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPasswordHash(), Collections.emptyList());
    }
}
