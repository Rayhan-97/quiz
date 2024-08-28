package com.quiz.services;

import com.quiz.core.entities.RefreshToken;
import com.quiz.core.entities.User;
import com.quiz.core.repositories.RefreshTokenRepository;
import com.quiz.core.repositories.UserRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class DefaultRefreshTokenServiceTest
{
    private static final User USER =
            new User("username", "email@email.com", "passwordHash");

    private final RefreshTokenRepository refreshTokenRepositoryMock;
    private final DefaultRefreshTokenService refreshTokenService;

    public DefaultRefreshTokenServiceTest()
    {
        this.refreshTokenRepositoryMock = mock(RefreshTokenRepository.class);
        this.refreshTokenService = new DefaultRefreshTokenService(
                100,
                refreshTokenRepositoryMock,
                mock(UserRepository.class)
        );
    }

    @Test
    void givenUser_whenCreateRefreshToken_thenTokenReturned()
    {
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(USER);

        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken.getUser()).isEqualTo(USER);

        verify(refreshTokenRepositoryMock).save(refreshToken);
    }

    @Test
    void givenUserAndTokenAlreadyExists_whenCreateRefreshToken_thenOldTokenDeletedAndNewTokenReturned()
    {
        RefreshToken userToken = new RefreshToken(USER, "token", Instant.MIN);
        when(refreshTokenRepositoryMock.findByUser(USER)).thenReturn(Optional.of(userToken));

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(USER);

        verify(refreshTokenRepositoryMock).delete(userToken);

        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken.getUser()).isEqualTo(USER);

        verify(refreshTokenRepositoryMock).save(refreshToken);
    }

    @Test
    void givenExpiredToken_whenVerify_thenThrow()
    {
        Instant oneMinuteAgo = Instant.now().minusSeconds(60);
        RefreshToken token = new RefreshToken(USER, "token", oneMinuteAgo);

        assertThatThrownBy(() -> refreshTokenService.verifyExpirationOrThrow(token))
                .isInstanceOf(RefreshTokenService.RefreshTokenExpiredException.class);
    }
}
