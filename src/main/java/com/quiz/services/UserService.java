package com.quiz.services;

import com.quiz.services.dtos.UserDto;

public interface UserService
{
    void registerNewUser(UserDto userDto) throws UserAlreadyExistsException;

    class UserAlreadyExistsException extends Exception
    {
        public UserAlreadyExistsException(String email)
        {
            super("User with email [%s] already exists".formatted(email));
        }
    }
}
