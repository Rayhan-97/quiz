package com.quiz.core.repositories;

import com.quiz.core.entities.RefreshToken;
import com.quiz.core.entities.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository
{
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    void delete(RefreshToken refreshToken);

    <S extends RefreshToken> RefreshToken save(S refreshToken);

    @Transactional
    int deleteByUserId(Long id);
}