package com.quiz.core.validators;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DefaultEmailValidatorTest
{
    private final EmailValidator emailValidator = new DefaultEmailValidator();

    @ParameterizedTest
    @ValueSource(strings = {
            "username@domain.com",
            "user.name@domain.com",
            "user-name@domain.com",
            "username@domain.co.in",
            "user_name@domain.com"
    })
    void givenValidEmail_whenValidate_thenTrue(String validEmail)
    {
        boolean validated = emailValidator.validate(validEmail);

        Assertions.assertThat(validated).isTrue();
    }

    @Test
    void givenNullEmail_whenValidate_thenFalse()
    {
        boolean validated = emailValidator.validate(null);

        Assertions.assertThat(validated).isFalse();
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
            "username.@domain.com",
            ".user.name@domain.com",
            "user-name@domain.com.",
            "username@.com"
    })
    void givenInvalidEmail_whenValidate_thenFalse(String invalidEmail)
    {
        boolean validated = emailValidator.validate(invalidEmail);

        Assertions.assertThat(validated).isFalse();
    }
}