package com.quiz.configuration.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenGenerator
{
    private final JwtSecretProvider jwtSecretProvider;
    private final JwtTokenExpirationProvider jwtTokenExpirationProvider;

    @Autowired
    public JwtTokenGenerator(JwtSecretProvider jwtSecretProvider, JwtTokenExpirationProvider jwtTokenExpirationProvider)
    {
        this.jwtSecretProvider = jwtSecretProvider;
        this.jwtTokenExpirationProvider = jwtTokenExpirationProvider;
    }

    public String generate(String email)
    {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiryDate = new Date(now + jwtTokenExpirationProvider.get());

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(issuedAt)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecretProvider.get())
                .compact();
    }
}
