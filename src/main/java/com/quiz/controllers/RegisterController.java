package com.quiz.controllers;

import com.quiz.controllers.dtos.UserDto;
import com.quiz.core.error.ErrorResponse;
import com.quiz.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @ControllerAdvice
    private static class InvalidDataSubmissionAdvice
    {
        private static final Map<String, ErrorResponse> ERROR_RESPONSE_MAP = Map.of(
                "username", ErrorResponse.REGISTRATION_USERNAME_ERROR,
                "email", ErrorResponse.REGISTRATION_EMAIL_ERROR,
                "password", ErrorResponse.REGISTRATION_PASSWORD_ERROR
        );

        @ResponseBody
        @ExceptionHandler(MethodArgumentNotValidException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        ErrorResponse invalidDataSubmissionHandler(MethodArgumentNotValidException exception)
        {
            FieldError fieldError = exception.getBindingResult().getFieldError();
            String field = fieldError == null ? "" : fieldError.getField();

            return ERROR_RESPONSE_MAP.getOrDefault(field, ErrorResponse.REGISTRATION_GENERIC_ERROR);
        }
    }
}
