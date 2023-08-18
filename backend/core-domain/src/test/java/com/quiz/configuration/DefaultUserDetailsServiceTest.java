package com.quiz.configuration;

import com.quiz.core.entities.User;
import com.quiz.core.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultUserDetailsServiceTest
{
    private UserRepository userRepository;
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp()
    {
        userRepository = mock(UserRepository.class);
        userDetailsService = new DefaultUserDetailsService(userRepository);
    }

    @Test
    void givenEmail_whenLoad_thenCorrectUserDetails()
    {
        String email = "user@email.com";
        User user = new User("username", email, "passwordHash");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertThat(userDetails.getUsername()).isEqualTo(user.getUsername());
        assertThat(userDetails.getPassword()).isEqualTo(user.getPasswordHash());
    }

    @Test
    void givenEmailAndNoUserWithEmail_whenLoad_thenThrowUsernameNotFoundException()
    {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("user@email.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}