package com.quiz.configuration.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.quiz.configuration.jwt.JwtTokenValidator.JwtTokenValidationResult.fail;
import static com.quiz.configuration.jwt.JwtTokenValidator.JwtTokenValidationResult.ok;

@Component
public class JwtTokenValidator
{
    private final JwtSecretProvider jwtSecretProvider;

    @Autowired
    public JwtTokenValidator(JwtSecretProvider jwtSecretProvider)
    {
        this.jwtSecretProvider = jwtSecretProvider;
    }

    public JwtTokenValidationResult validate(String token)
    {
        try
        {
            Jwts.parser()
                .setSigningKey(jwtSecretProvider.get())
                .parseClaimsJws(token);

            return ok();
        }
        catch (SignatureException e)
        {
            return fail("Invalid JWT signature - " + e.getMessage());
        }
        catch (MalformedJwtException e)
        {
            return fail("Invalid JWT token - " + e.getMessage());
        }
        catch (ExpiredJwtException e)
        {
            return fail("Expired JWT token - " + e.getMessage());
        }
        catch (UnsupportedJwtException e)
        {
            return fail("Unsupported JWT token - " + e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            return fail("JWT claims string is empty - " + e.getMessage());
        }
    }

    public record JwtTokenValidationResult(boolean success, String failureReason)
    {
        public static JwtTokenValidationResult ok()
        {
            return new JwtTokenValidationResult(true, null);
        }

        public static JwtTokenValidationResult fail(String failureReason)
        {
            return new JwtTokenValidationResult(false, failureReason);
        }
    }
}
