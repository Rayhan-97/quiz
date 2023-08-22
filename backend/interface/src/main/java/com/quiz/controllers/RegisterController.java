package com.quiz.controllers;

import com.quiz.controllers.dtos.UserDto;
import com.quiz.core.error.ErrorResponse;
import com.quiz.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@SuppressWarnings("unused")
@RestController
public class RegisterController
{
    private final UserService userService;

    @Autowired
    public RegisterController(UserService userService)
    {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public void register(@RequestBody @Valid UserDto userDto) throws UserService.UserAlreadyExistsException
    {
        userService.registerNewUser(userDto.convert());
    }

    @ControllerAdvice
    private static class UserAlreadyExistsAdvice
    {
        @ResponseBody
        @ExceptionHandler(UserService.UserAlreadyExistsException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        ErrorResponse userAlreadyExistsHandler(UserService.UserAlreadyExistsException ex) {
            return ErrorResponse.REGISTRATION_USER_ALREADY_EXISTS_ERROR;
        }
    }
}
