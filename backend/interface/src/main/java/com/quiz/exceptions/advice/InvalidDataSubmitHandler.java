package com.quiz.exceptions.advice;

import com.quiz.core.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;
import java.util.Optional;

interface InvalidDataSubmitHandler
{
    Optional<ErrorResponse> handle(MethodArgumentNotValidException exception, HttpServletRequest request);

    static Optional<ErrorResponse> getErrorResponse(MethodArgumentNotValidException exception,
                                                    HttpServletRequest request,
                                                    String endpoint,
                                                    Map<String, ErrorResponse> errorResponseMap,
                                                    ErrorResponse fallbackErrorResponse)
    {
        String requestedEndpoint = request.getRequestURI();
        if (!endpoint.equals(requestedEndpoint))
        {
            return Optional.empty();
        }

        FieldError fieldError = exception.getBindingResult().getFieldError();
        String field = fieldError == null ? "" : fieldError.getField();

        return Optional.of(errorResponseMap.getOrDefault(field, fallbackErrorResponse));
    }
}
