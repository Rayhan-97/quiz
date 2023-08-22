package com.quiz.exceptions.advice;

import com.quiz.core.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Optional;

@ControllerAdvice
public class InvalidSubmissionDataAdvice
{
    private final List<InvalidDataSubmitHandler> invalidDataSubmitHandlers;

    @Autowired
    public InvalidSubmissionDataAdvice(RegisterInvalidDataSubmitHandler registerHandler, LoginInvalidDataSubmitHandler loginHandler)
    {
        this.invalidDataSubmitHandlers = List.of(
                registerHandler,
                loginHandler
        );
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse invalidDataSubmissionHandler(MethodArgumentNotValidException exception, HttpServletRequest request)
    {
        for (InvalidDataSubmitHandler handler : invalidDataSubmitHandlers)
        {
            Optional<ErrorResponse> errorResponse = handler.handle(exception, request);
            if (errorResponse.isPresent())
            {
                return errorResponse.get();
            }
        }

        return ErrorResponse.UNEXPECTED_ERROR;
    }
}
