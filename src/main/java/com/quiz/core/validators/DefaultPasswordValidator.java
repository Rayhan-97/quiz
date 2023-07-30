package com.quiz.core.validators;

import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Service;

@Service
public class DefaultPasswordValidator implements PasswordValidator
{
    public static final int MINIMUM_PASSWORD_LENGTH = 8;

    @Override
    public boolean validate(String password)
    {
        return password != null && password.length() >= MINIMUM_PASSWORD_LENGTH;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context)
    {
        return validate(value);
    }
}
