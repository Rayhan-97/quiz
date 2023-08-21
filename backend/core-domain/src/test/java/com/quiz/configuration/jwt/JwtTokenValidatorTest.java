package com.quiz.configuration.jwt;

import com.quiz.configuration.jwt.JwtTokenValidator.JwtTokenValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtTokenValidatorTest
{

    private JwtSecretProvider secretProvider;
    private JwtTokenExpirationProvider expirationProvider;
    private JwtTokenGenerator tokenGenerator;

    @BeforeEach
    void setUp()
    {
        secretProvider = mock(JwtSecretProvider.class);
        expirationProvider = mock(JwtTokenExpirationProvider.class);
        tokenGenerator = new JwtTokenGenerator(secretProvider, expirationProvider);
    }

    @Test
    void givenValidToken_whenValidate_thenSuccessful()
    {
        when(secretProvider.get()).thenReturn("test-secret");
        when(expirationProvider.get()).thenReturn(5000L);

        String token = tokenGenerator.generate("user@email.com");

        JwtTokenValidator jwtTokenValidator = new JwtTokenValidator(secretProvider);

        JwtTokenValidationResult validationResult = jwtTokenValidator.validate(token);

        assertThat(validationResult.success()).isTrue();
    }

    @Test
    void givenInvalidTokenWithDifferentSignatures_whenValidate_thenFail()
    {
        when(secretProvider.get()).thenReturn("test-secret");
        when(expirationProvider.get()).thenReturn(5000L);

        String token = tokenGenerator.generate("user@email.com");

        JwtSecretProvider validatorSecretProvider = mock(JwtSecretProvider.class);
        when(validatorSecretProvider.get()).thenReturn("different-secret");

        JwtTokenValidator jwtTokenValidator = new JwtTokenValidator(validatorSecretProvider);

        JwtTokenValidationResult validationResult = jwtTokenValidator.validate(token);

        assertThat(validationResult.success()).isFalse();
        assertThat(validationResult.failureReason()).contains("Invalid JWT signature");
    }

    @Test
    void givenInvalidTokenThatHasExpired_whenValidate_thenFail() throws InterruptedException
    {
        when(secretProvider.get()).thenReturn("test-secret");
        when(expirationProvider.get()).thenReturn(0L);

        String token = tokenGenerator.generate("user@email.com");

        JwtTokenValidator jwtTokenValidator = new JwtTokenValidator(secretProvider);

        sleep(50);
        JwtTokenValidationResult validationResult = jwtTokenValidator.validate(token);

        assertThat(validationResult.success()).isFalse();
        assertThat(validationResult.failureReason()).contains("Expired JWT token");
    }

    @Test
    void givenIllegalInvalidToken_whenValidate_thenFail()
    {
        JwtTokenValidator jwtTokenValidator = new JwtTokenValidator(secretProvider);

        JwtTokenValidationResult validationResult = jwtTokenValidator.validate("illegal");

        assertThat(validationResult.success()).isFalse();
        assertThat(validationResult.failureReason()).contains("JWT claims string is empty");
    }

    @Test
    void givenNullInvalidToken_whenValidate_thenFail()
    {
        JwtTokenValidator jwtTokenValidator = new JwtTokenValidator(secretProvider);

        JwtTokenValidationResult validationResult = jwtTokenValidator.validate(null);

        assertThat(validationResult.success()).isFalse();
        assertThat(validationResult.failureReason()).contains("JWT claims string is empty");
    }
}