package com.quiz.core.validators;

import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class DefaultPasswordValidator implements PasswordValidator
{
    public static final int MINIMUM_PASSWORD_LENGTH = 8;

    @Override
    public void validate(String password)
    {
        Objects.requireNonNull(password);

        if (password.length() < MINIMUM_PASSWORD_LENGTH)
        {
            throw new IllegalArgumentException("Password is too short");
        }
    }
}
