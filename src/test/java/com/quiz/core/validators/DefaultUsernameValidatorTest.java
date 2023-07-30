package com.quiz.core.validators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultUsernameValidatorTest
{
    private final UsernameValidator usernameValidator = new DefaultUsernameValidator();

    @Test
    void givenValidUsername_whenValidate_thenTrue()
    {
        String validUsername = "a123-_";
        boolean validated = usernameValidator.validate(validUsername);

        assertThat(validated).isTrue();
    }

    @Test
    void givenNullUsername_whenValidate_thenFalse()
    {
        boolean validated = usernameValidator.validate(null);

        assertThat(validated).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "*",
            "+",
            "=",
            "@",
            "£",
            "#",
            "$",
            "%",
            "12345",
    })
    void givenInvalidUsername_whenValidate_thenFalse(String invalidUsername)
    {
        boolean validated = usernameValidator.validate(invalidUsername);

        assertThat(validated).isFalse();
    }
}