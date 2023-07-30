package com.quiz.core.validators;

import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DefaultUsernameValidator implements UsernameValidator
{
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[_A-Za-z0-9-]{6,}$");

    @Override
    public boolean validate(String username)
    {
        if (username == null)
        {
            return false;
        }

        Matcher matcher = USERNAME_PATTERN.matcher(username);

        return matcher.matches();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context)
    {
        return validate(value);
    }
}
