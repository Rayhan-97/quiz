package com.quiz.core.validators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultUsernameValidatorTest
{
    private final UsernameValidator usernameValidator = new DefaultUsernameValidator();

    @Test
    void givenValidUsername_whenValidate_thenDoNothing()
    {
        String validUsername = "a123-_";
        usernameValidator.validate(validUsername);
    }

    @Test
    void givenNullUsername_whenValidate_thenNullPointerException()
    {
        assertThatThrownBy(() -> usernameValidator.validate(null))
                .isInstanceOf(NullPointerException.class);
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
    void givenInvalidUsername_whenValidate_thenIllegalArgumentException(String invalidUsername)
    {
        assertThatThrownBy(() -> usernameValidator.validate(invalidUsername))
                .isInstanceOf(IllegalArgumentException.class);
    }
}