package com.quiz.configuration.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenParser
{
    private final JwtSecretProvider jwtSecretProvider;

    @Autowired
    public JwtTokenParser(JwtSecretProvider jwtSecretProvider)
    {
        this.jwtSecretProvider = jwtSecretProvider;
    }

    public String getEmail(String token)
    {
        Claims claims = Jwts.parser()
            .setSigningKey(jwtSecretProvider.get())
            .parseClaimsJws(token)
            .getBody();

        return claims.getSubject();
    }
}
