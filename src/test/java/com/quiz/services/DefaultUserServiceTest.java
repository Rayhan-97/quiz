package com.quiz.services;

import com.quiz.core.entities.NewUserDto;
import com.quiz.core.entities.User;
import com.quiz.core.repositories.UserRepository;
import com.quiz.core.validators.EmailValidator;
import com.quiz.core.validators.PasswordValidator;
import com.quiz.core.validators.UsernameValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;

class DefaultUserServiceTest
{
    private final UserRepository userRepository;
    private final PasswordHashGenerator passwordHashGenerator;
    private final UsernameValidator usernameValidator;
    private final PasswordValidator passwordValidator;
    private final EmailValidator emailValidator;
    private final UserService userService;

    DefaultUserServiceTest()
    {
        userRepository = mock(UserRepository.class);
        passwordHashGenerator = mock(PasswordHashGenerator.class);
        usernameValidator = mock(UsernameValidator.class);
        passwordValidator = mock(PasswordValidator.class);
        emailValidator = mock(EmailValidator.class);
        userService = new DefaultUserService(userRepository, passwordHashGenerator, usernameValidator, passwordValidator, emailValidator);
    }

    @Test
    public void givenNewUser_whenRegister_thenSaveNewUser() throws UserService.UserAlreadyExistsException
    {
        String username = "username";
        String email = "test@test";
        String password = "password";
        NewUserDto newUserDto = new NewUserDto(username, email, password);

        String passwordHash = "super-secret-hash";
        when(passwordHashGenerator.generateHash(password)).thenReturn(passwordHash);

        User expectedUser = new User();
        expectedUser.setUsername(username);
        expectedUser.setEmail(email);
        expectedUser.setPasswordHash(passwordHash);

        userService.registerNewUser(newUserDto);

        verify(userRepository).save(expectedUser);
    }

    @Test
    public void givenNewUserWithEmailAlreadyExists_whenRegister_thenUserAlreadyExistsException()
    {
        String email = "test@test";
        User user = new User();
        user.setEmail(email);
        Optional<User> optionalUser = Optional.of(user);
        when(userRepository.findByEmail(email)).thenReturn(optionalUser);

        NewUserDto newUserDto = new NewUserDto("username", email, "password");

        Assertions.assertThatThrownBy(() -> userService.registerNewUser(newUserDto))
                .isInstanceOf(UserService.UserAlreadyExistsException.class);
    }

    @Test
    public void givenNewUserWithInvalidPassword_whenRegister_thenIllegalArgumentException()
    {
        String invalidPassword = "invalid";
        NewUserDto newUserDto = new NewUserDto("username", "email", invalidPassword);

        doThrow(IllegalArgumentException.class).when(passwordValidator).validate(invalidPassword);

        Assertions.assertThatThrownBy(() -> userService.registerNewUser(newUserDto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void givenNewUserWithInvalidUsername_whenRegister_thenIllegalArgumentException()
    {
        String invalidUsername = "invalid";
        NewUserDto newUserDto = new NewUserDto(invalidUsername, "email", "password");

        doThrow(IllegalArgumentException.class).when(usernameValidator).validate(invalidUsername);

        Assertions.assertThatThrownBy(() -> userService.registerNewUser(newUserDto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void givenNewUserWithInvalidEmail_whenRegister_thenIllegalArgumentException()
    {
        String invalidEmail = "invalid";
        NewUserDto newUserDto = new NewUserDto("username", invalidEmail, "password");

        doThrow(IllegalArgumentException.class).when(emailValidator).validate(invalidEmail);

        Assertions.assertThatThrownBy(() -> userService.registerNewUser(newUserDto))
                .isInstanceOf(IllegalArgumentException.class);
    }
}