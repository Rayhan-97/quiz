package com.quiz.exceptions.advice;

import com.quiz.core.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;
import java.util.Optional;

import static com.quiz.exceptions.advice.InvalidDataSubmitHandler.getErrorResponse;

@Component
class LoginInvalidDataSubmitHandler implements InvalidDataSubmitHandler
{
    private static final String ENDPOINT = "/login";
    private static final Map<String, ErrorResponse> ERROR_RESPONSE_MAP = Map.of(
            "email", ErrorResponse.LOGIN_EMAIL_ERROR,
            "password", ErrorResponse.LOGIN_PASSWORD_ERROR
    );

    public Optional<ErrorResponse> handle(MethodArgumentNotValidException exception, HttpServletRequest request)
    {
        return getErrorResponse(exception, request, ENDPOINT, ERROR_RESPONSE_MAP, ErrorResponse.LOGIN_GENERIC_ERROR);
    }
}
