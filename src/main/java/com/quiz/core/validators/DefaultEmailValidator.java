package com.quiz.core.validators;

import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DefaultEmailValidator implements EmailValidator
{
    private static final String EMAIL_PATTERN_REGEX = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@" +
                                                      "[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_PATTERN_REGEX);

    @Override
    public boolean validate(String email)
    {
        if (email == null)
        {
            return false;
        }

        Matcher matcher = EMAIL_PATTERN.matcher(email);

        return matcher.matches();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context)
    {
        return validate(value);
    }
}
