package com.quiz.services;

public interface PasswordHashGenerator
{
    String generateHash(String password);

    boolean matches(String rawPassword, String encodedPassword);
}
