package com.quiz.services;

import com.quiz.core.entities.RefreshToken;
import com.quiz.core.entities.User;
import com.quiz.core.repositories.RefreshTokenRepository;
import com.quiz.core.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class DefaultRefreshTokenService implements RefreshTokenService
{
    private static final long REFRESH_TOKEN_EXPIRY_MS = 24 * 60 * 60 * 1000;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Autowired
    public DefaultRefreshTokenService(
            RefreshTokenRepository refreshTokenRepository, UserRepository userRepository
    )
    {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token)
    {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken createRefreshToken(User user)
    {
        Optional<RefreshToken> existingRefreshToken = refreshTokenRepository.findByUser(user);
        existingRefreshToken.ifPresent(refreshTokenRepository::delete);

        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusMillis(REFRESH_TOKEN_EXPIRY_MS);
        RefreshToken refreshToken = new RefreshToken(user, token, expiryDate);

        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken refreshToken)
    {
        Instant expiryDate = refreshToken.getExpiryDate();
        Instant now = Instant.now();

        if (expiryDate.isBefore(now))
        {
            refreshTokenRepository.delete(refreshToken);
            throw new RefreshTokenExpiredException(refreshToken.getToken());
        }

        return refreshToken;
    }

    @Override
    public boolean deleteByUserDetails(String email)
    {
        Optional<User> user = userRepository.findByEmail(email);

        return user
                .map(User::getId)
                .map(refreshTokenRepository::deleteByUserId)
                .map(i -> i == 1)
                .orElse(false);
    }

}
