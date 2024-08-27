package com.quiz.services;

import com.quiz.core.entities.RefreshToken;
import com.quiz.core.entities.User;

import java.util.Optional;

public interface RefreshTokenService
{
    Optional<RefreshToken> findByToken(String token);

    RefreshToken createRefreshToken(User user);

    RefreshToken verifyExpiration(RefreshToken refreshToken);

    boolean deleteByUserDetails(String email);

    class RefreshTokenExpiredException extends RuntimeException
    {
        public RefreshTokenExpiredException(String token)
        {
            super("Refresh token [%s] has already expired".formatted(token));
        }
    }
}
