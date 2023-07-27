package com.quiz.services;

import com.quiz.core.entities.NewUserDto;

public interface UserService
{
    void registerNewUser(NewUserDto newUserDto) throws UserAlreadyExistsException;

    class UserAlreadyExistsException extends Exception
    {
        public UserAlreadyExistsException(String email)
        {
            super("User with email [%s] already exists".formatted(email));
        }
    }
}
