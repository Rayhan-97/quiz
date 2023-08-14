package com.quiz.services;

public interface PasswordHashGenerator
{
    String generateHash(String password);
}
