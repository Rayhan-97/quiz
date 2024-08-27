package com.quiz.core.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@SuppressWarnings("unused")
@Entity
@Getter
@Setter
@EqualsAndHashCode
public class RefreshToken
{
    @Id
    @GeneratedValue
    long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(unique = true, nullable = false)
    String token;

    @Column(nullable = false)
    Instant expiryDate;

    private RefreshToken()
    {
        // no args jackson constructor
    }

    public RefreshToken(User user, String token, Instant expiryDate)
    {
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
    }
}
