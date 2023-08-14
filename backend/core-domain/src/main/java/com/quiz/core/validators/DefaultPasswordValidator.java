package com.quiz.core.validators;

import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Service;

@Service
public class DefaultPasswordValidator implements PasswordValidator
{
    private static final int MINIMUM_PASSWORD_LENGTH = 8;
    private static final int MAXIMUM_PASSWORD_LENGTH = 128;

    @Override
    public boolean validate(String password)
    {
        return password != null && password.length() >= MINIMUM_PASSWORD_LENGTH && password.length() < MAXIMUM_PASSWORD_LENGTH;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context)
    {
        return validate(value);
    }
}
