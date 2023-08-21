package com.quiz.configuration.jwt;

import org.springframework.stereotype.Component;

@Component
public class JwtTokenExpirationProvider
{
    private static final String JWT_EXPIRATION_ENV_VARIABLE = "JWT_EXPIRATION";
    private static final long ONE_DAY_MILLIS = 24 * 60 * 60 * 1000;

    public long get()
    {
        String jwtExpiration = System.getenv(JWT_EXPIRATION_ENV_VARIABLE);

        return jwtExpiration != null ? Long.parseLong(jwtExpiration) : ONE_DAY_MILLIS;
    }
}
