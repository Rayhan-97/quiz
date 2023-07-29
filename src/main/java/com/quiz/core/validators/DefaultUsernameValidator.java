package com.quiz.core.validators;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultUsernameValidator implements UsernameValidator
{
    private static final String USERNAME_PATTERN_REGEX = "^[_A-Za-z0-9-]{6,}$";
    private static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_PATTERN_REGEX);

    @Override
    public void validate(String username)
    {
        Objects.requireNonNull(username);

        Matcher matcher = USERNAME_PATTERN.matcher(username);
        if (!matcher.matches())
        {
            throw new IllegalArgumentException("Invalid username");
        }
    }
}
