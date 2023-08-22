package com.quiz.controllers;

import com.quiz.controllers.dtos.UserCredentialsDto;
import com.quiz.core.error.ErrorResponse;
import com.quiz.services.LoginService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@SuppressWarnings("unused")
@RestController
public class LoginController
{
    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService)
    {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public String login(@RequestBody @Valid UserCredentialsDto userCredentialsDto)
    {
        return loginService.retrieveLoginToken(userCredentialsDto.convert());
    }

    @ControllerAdvice
    private static class BadCredentialsAdvice
    {
        @ResponseBody
        @ExceptionHandler(BadCredentialsException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        ErrorResponse badCredentialsHandler(BadCredentialsException ex) {
            return ErrorResponse.LOGIN_BAD_CREDENTIALS_ERROR;
        }
    }
}
