package com.quiz.core.validators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

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
    @MethodSource("invalidUsernames")
    void givenInvalidUsername_whenValidate_thenFalse(String invalidUsername)
    {
        boolean validated = usernameValidator.validate(invalidUsername);

        assertThat(validated).isFalse();
    }

    private static Stream<Arguments> invalidUsernames()
    {
        return Stream.of(
                null,
                arguments(""),
                arguments("*"),
                arguments("+"),
                arguments("="),
                arguments("@"),
                arguments("£"),
                arguments("#"),
                arguments("$"),
                arguments("%"),
                arguments("12345"),
                arguments("a".repeat(32 + 1))
        );
    }
}