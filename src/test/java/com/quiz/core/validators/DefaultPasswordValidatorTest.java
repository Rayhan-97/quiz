package com.quiz.core.validators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPasswordValidatorTest
{
    private final PasswordValidator passwordValidator = new DefaultPasswordValidator();

    @Test
    void givenValidPassword_whenValidate_thenTrue()
    {
        String validPassword = "password";
        boolean validated = passwordValidator.validate(validPassword);

        assertThat(validated).isTrue();
    }

    @Test
    void givenNullPassword_whenValidate_thenFalse()
    {
        boolean validated = passwordValidator.validate(null);

        assertThat(validated).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "1", "22", "333", "4444", "55555", "666666", "7777777"})
    void givenInvalidPasswordTooShort_whenValidate_thenFalse(String invalidShortPassword)
    {
        boolean validated = passwordValidator.validate(invalidShortPassword);

        assertThat(validated).isFalse();
    }
}