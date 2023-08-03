package com.quiz.services;

import com.quiz.core.entities.User;
import com.quiz.core.repositories.UserRepository;
import com.quiz.services.dtos.UserDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;

class DefaultUserServiceTest
{
    private final UserRepository userRepository;
    private final PasswordHashGenerator passwordHashGenerator;
    private final UserService userService;

    DefaultUserServiceTest()
    {
        userRepository = mock(UserRepository.class);
        passwordHashGenerator = mock(PasswordHashGenerator.class);
        userService = new DefaultUserService(userRepository, passwordHashGenerator);
    }

    @Test
    public void givenNewUser_whenRegister_thenSaveNewUser() throws UserService.UserAlreadyExistsException
    {
        String username = "username";
        String email = "test@test";
        String password = "password";
        UserDto userDto = new UserDto(username, email, password);

        String passwordHash = "super-secret-hash";
        when(passwordHashGenerator.generateHash(password)).thenReturn(passwordHash);

        User expectedUser = new User(username, email, passwordHash);

        userService.registerNewUser(userDto);

        verify(userRepository).save(expectedUser);
    }

    @Test
    public void givenNewUserWithEmailAlreadyExists_whenRegister_thenUserAlreadyExistsException()
    {
        String email = "test@test";
        User user = new User("username", email, "password hash");
        Optional<User> optionalUser = Optional.of(user);
        when(userRepository.findByEmail(email)).thenReturn(optionalUser);

        UserDto userDto = new UserDto("username", email, "password");

        Assertions.assertThatThrownBy(() -> userService.registerNewUser(userDto))
                .isInstanceOf(UserService.UserAlreadyExistsException.class);
    }
}