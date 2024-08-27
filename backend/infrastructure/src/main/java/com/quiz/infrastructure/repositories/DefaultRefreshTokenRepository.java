package com.quiz.infrastructure.repositories;

import com.quiz.core.entities.RefreshToken;
import com.quiz.core.repositories.RefreshTokenRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefaultRefreshTokenRepository extends RefreshTokenRepository, CrudRepository<RefreshToken, Long>
{
}
