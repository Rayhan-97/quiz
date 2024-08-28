package com.quiz.core.error;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorResponse
{
    UNEXPECTED_ERROR(new ErrorResponseBody(9000, ErrorResponse.UNEXPECTED_ERROR_MESSAGE)),

    REGISTRATION_GENERIC_ERROR(new ErrorResponseBody(1000, ErrorResponse.REGISTRATION_ERROR_MESSAGE)),
    REGISTRATION_USERNAME_ERROR(new ErrorResponseBody(1001, ErrorResponse.REGISTRATION_ERROR_MESSAGE)),
    REGISTRATION_EMAIL_ERROR(new ErrorResponseBody(1002, ErrorResponse.REGISTRATION_ERROR_MESSAGE)),
    REGISTRATION_PASSWORD_ERROR(new ErrorResponseBody(1003, ErrorResponse.REGISTRATION_ERROR_MESSAGE)),
    REGISTRATION_USER_ALREADY_EXISTS_ERROR(new ErrorResponseBody(1004, ErrorResponse.REGISTRATION_ERROR_MESSAGE)),

    LOGIN_GENERIC_ERROR(new ErrorResponseBody(1100, ErrorResponse.LOGIN_ERROR_MESSAGE)),
    LOGIN_EMAIL_ERROR(new ErrorResponseBody(1101, ErrorResponse.LOGIN_ERROR_MESSAGE)),
    LOGIN_PASSWORD_ERROR(new ErrorResponseBody(1102, ErrorResponse.LOGIN_ERROR_MESSAGE)),
    LOGIN_BAD_CREDENTIALS_ERROR(new ErrorResponseBody(1103, ErrorResponse.LOGIN_ERROR_MESSAGE)),

    REFRESH_TOKEN_COOKIE_MISSING_ERROR(new ErrorResponseBody(1200, ErrorResponse.REFRESH_TOKEN_COOKIE_MISSING_ERROR_MESSAGE)),
    REFRESH_TOKEN_COOKIE_UNKNOWN_ERROR(new ErrorResponseBody(1201, ErrorResponse.REFRESH_TOKEN_COOKIE_ERROR_MESSAGE)),
    REFRESH_TOKEN_COOKIE_EXPIRED_ERROR(new ErrorResponseBody(1202, ErrorResponse.REFRESH_TOKEN_COOKIE_ERROR_MESSAGE)),

    ;

    public static final String UNEXPECTED_ERROR_MESSAGE = "Oh oh something went wrong";
    public static final String REGISTRATION_ERROR_MESSAGE = "Invalid username, email or password";
    public static final String LOGIN_ERROR_MESSAGE = "Invalid email or password";
    public static final String REFRESH_TOKEN_COOKIE_MISSING_ERROR_MESSAGE = "Missing refresh token cookie";
    public static final String REFRESH_TOKEN_COOKIE_ERROR_MESSAGE = "Invalid refresh token cookie";

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
