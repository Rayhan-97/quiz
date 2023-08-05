package com.quiz.core.validators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

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
    @MethodSource("invalidStrings")
    void givenInvalidPasswordTooShort_whenValidate_thenFalse(String invalidShortPassword)
    {
        boolean validated = passwordValidator.validate(invalidShortPassword);

        assertThat(validated).isFalse();
    }

    private static Stream<Arguments> invalidStrings()
    {
        return Stream.of(
                null,
                arguments(""),
                arguments("1"),
                arguments("22"),
                arguments("333"),
                arguments("4444"),
                arguments("55555"),
                arguments("666666"),
                arguments("7777777"),
                arguments("a".repeat(128))
        );
    }
}