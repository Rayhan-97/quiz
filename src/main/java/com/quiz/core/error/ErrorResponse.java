package com.quiz.core.error;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorResponse
{
    REGISTRATION_GENERIC_ERROR(new ErrorResponseBody(1000, ErrorResponse.REGISTRATION_ERROR_MESSAGE)),
    REGISTRATION_USERNAME_ERROR(new ErrorResponseBody(1001, ErrorResponse.REGISTRATION_ERROR_MESSAGE)),
    REGISTRATION_EMAIL_ERROR(new ErrorResponseBody(1002, ErrorResponse.REGISTRATION_ERROR_MESSAGE)),
    REGISTRATION_PASSWORD_ERROR(new ErrorResponseBody(1003, ErrorResponse.REGISTRATION_ERROR_MESSAGE)),
    REGISTRATION_USER_ALREADY_EXISTS_ERROR(new ErrorResponseBody(1004, ErrorResponse.REGISTRATION_ERROR_MESSAGE)),
    ;

    public static final String REGISTRATION_ERROR_MESSAGE = "Invalid username, email or password";

    private final ErrorResponseBody body;

    ErrorResponse(ErrorResponseBody body)
    {
        this.body = body;
    }

    @JsonValue
    public ErrorResponseBody body()
    {
        return body;
    }

    public record ErrorResponseBody(int errorCode, String errorMessage)
    {
    }
}
