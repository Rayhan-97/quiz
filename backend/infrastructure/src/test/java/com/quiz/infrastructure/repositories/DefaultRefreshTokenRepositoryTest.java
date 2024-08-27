package com.quiz.infrastructure.repositories;

import com.quiz.core.entities.RefreshToken;
import com.quiz.core.entities.User;
import com.quiz.core.repositories.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ContextConfiguration(classes = DefaultRefreshTokenRepository.class)
@EnableJpaRepositories(basePackageClasses = DefaultRefreshTokenRepository.class)
@EntityScan(basePackages = "com.quiz")
class DefaultRefreshTokenRepositoryTest
{
    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Test
    void givenRefreshTokenRepositoryHasToken_whenDeleteTokenByUserId_thenTokenIsDeleted()
    {
        User user = new User("username", "email@email.com", "passwordHash");
        RefreshToken refreshToken = new RefreshToken(user, "refreshToken", Instant.now());

        testEntityManager.persistAndFlush(user);
        testEntityManager.persistAndFlush(refreshToken);

        assertThat(refreshTokenRepository.findByToken(refreshToken.getToken()))
                .isPresent();

        int deleted = refreshTokenRepository.deleteByUserId(user.getId());
        assertThat(deleted).isEqualTo(1);

        assertThat(refreshTokenRepository.findByToken(refreshToken.getToken()))
                .isEmpty();
    }
}