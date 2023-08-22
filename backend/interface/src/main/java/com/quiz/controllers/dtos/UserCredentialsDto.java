package com.quiz.controllers.dtos;

import com.quiz.core.entities.validators.ValidEmail;
import com.quiz.core.entities.validators.ValidPassword;

public record UserCredentialsDto(
        @ValidEmail String email,
        @ValidPassword String password
)
{
    public com.quiz.services.dtos.UserCredentialsDto convert()
    {
        return new com.quiz.services.dtos.UserCredentialsDto(email, password);
    }
}
