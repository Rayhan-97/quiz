package com.quiz.exceptions.advice;

import com.quiz.core.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;
import java.util.Optional;

import static com.quiz.exceptions.advice.InvalidDataSubmitHandler.getErrorResponse;

@Component
class RegisterInvalidDataSubmitHandler implements InvalidDataSubmitHandler
{
    private static final String ENDPOINT = "/register";
    private static final Map<String, ErrorResponse> ERROR_RESPONSE_MAP = Map.of(
            "username", ErrorResponse.REGISTRATION_USERNAME_ERROR,
            "email", ErrorResponse.REGISTRATION_EMAIL_ERROR,
            "password", ErrorResponse.REGISTRATION_PASSWORD_ERROR
    );

    public Optional<ErrorResponse> handle(MethodArgumentNotValidException exception, HttpServletRequest request)
    {
        return getErrorResponse(exception, request, ENDPOINT, ERROR_RESPONSE_MAP, ErrorResponse.REGISTRATION_GENERIC_ERROR);
    }
}
