package com.quiz.services;

import org.springframework.stereotype.Service;

@Service
public class DefaultPasswordHashGenerator implements PasswordHashGenerator
{
    @Override
    public String generateHash(String password)
    {
        return password + "hash";
    }
}
