package com.quiz.core.validators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultEmailValidatorTest
{
    private final EmailValidator emailValidator = new DefaultEmailValidator();

    @Test
    void givenValidEmail_whenValidate_thenDoNothing()
    {
        String validEmail = "valid@email.com";
        emailValidator.validate(validEmail);
    }

    @Test
    void givenNullEmail_whenValidate_thenNullPointerException()
    {
        assertThatThrownBy(() -> emailValidator.validate(null))
                .isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "@",
            "@.",
            "@.co",
            "@a.co",
            "a@.co",
            "a@a.a",
            "*@a.co",
            "a@*.co",
    })
    void givenInvalidEmail_whenValidate_thenIllegalArgumentException(String invalidEmail)
    {
        assertThatThrownBy(() -> emailValidator.validate(invalidEmail))
                .isInstanceOf(IllegalArgumentException.class);
    }
}