package com.quiz.services;

import com.quiz.core.entities.NewUserDto;
import com.quiz.core.entities.User;
import com.quiz.core.repositories.UserRepository;
import com.quiz.core.validators.EmailValidator;
import com.quiz.core.validators.PasswordValidator;
import com.quiz.core.validators.UsernameValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setup()
    {
        when(emailValidator.validate(anyString())).thenReturn(true);
        when(usernameValidator.validate(anyString())).thenReturn(true);
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

        User expectedUser = new User(username, email, passwordHash);

        userService.registerNewUser(newUserDto);

        verify(userRepository).save(expectedUser);
    }

    @Test
    public void givenNewUserWithEmailAlreadyExists_whenRegister_thenUserAlreadyExistsException()
    {
        String email = "test@test";
        User user = new User("username", email, "password hash");
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

        when(usernameValidator.validate(invalidUsername)).thenReturn(false);

        Assertions.assertThatThrownBy(() -> userService.registerNewUser(newUserDto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void givenNewUserWithInvalidEmail_whenRegister_thenIllegalArgumentException()
    {
        String invalidEmail = "invalid";
        NewUserDto newUserDto = new NewUserDto("username", invalidEmail, "password");

        when(emailValidator.validate(invalidEmail)).thenReturn(false);

        Assertions.assertThatThrownBy(() -> userService.registerNewUser(newUserDto))
                .isInstanceOf(IllegalArgumentException.class);
    }
}