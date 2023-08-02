package com.quiz.controllers.dtos;

import com.quiz.core.entities.validators.ValidEmail;
import com.quiz.core.entities.validators.ValidPassword;
import com.quiz.core.entities.validators.ValidUsername;

public record UserDto(
        @ValidUsername String username,
        @ValidEmail String email,
        @ValidPassword String password
)
{
    public com.quiz.services.dtos.UserDto convert()
    {
        return new com.quiz.services.dtos.UserDto(username, email, password);
    }
}
