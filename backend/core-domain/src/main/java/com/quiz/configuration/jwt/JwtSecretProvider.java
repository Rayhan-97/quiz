package com.quiz.configuration.jwt;

import org.springframework.stereotype.Component;

@Component
public class JwtSecretProvider
{
    private static final String JWT_SECRET_ENV_VARIABLE = "JWT_SECRET";
    private static final String DEFAULT_JWT_SECRET = "secret";

    public String get()
    {
        String jwtSecret = System.getenv(JWT_SECRET_ENV_VARIABLE);

        return jwtSecret != null ? jwtSecret : DEFAULT_JWT_SECRET;
    }
}
