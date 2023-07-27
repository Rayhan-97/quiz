package com.quiz.core.validators;

import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DefaultEmailValidator implements EmailValidator
{
    private static final String EMAIL_PATTERN_REGEX = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_PATTERN_REGEX);

    @Override
    public void validate(String email)
    {
        Objects.requireNonNull(email);

        Matcher matcher = EMAIL_PATTERN.matcher(email);
        if (!matcher.matches())
        {
            throw new IllegalArgumentException("Invalid email");
        }
    }
}
