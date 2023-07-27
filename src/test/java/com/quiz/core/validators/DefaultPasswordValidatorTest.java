package com.quiz.core.validators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultPasswordValidatorTest
{
    private final PasswordValidator passwordValidator = new DefaultPasswordValidator();

    @Test
    void givenValidPassword_whenValidate_thenDoNothing()
    {
        String validPassword = "password";
        passwordValidator.validate(validPassword);
    }

    @Test
    void givenNullPassword_whenValidate_thenNullPointerException()
    {
        assertThatThrownBy(() -> passwordValidator.validate(null))
                .isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "1", "22", "333", "4444", "55555", "666666", "7777777"})
    void givenInvalidPasswordTooShort_whenValidate_thenIllegalArgumentException(String invalidShortPassword)
    {
        assertThatThrownBy(() -> passwordValidator.validate(invalidShortPassword))
                .isInstanceOf(IllegalArgumentException.class);
    }
}