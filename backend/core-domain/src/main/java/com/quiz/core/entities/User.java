package com.quiz.core.entities;

import com.quiz.core.entities.validators.ValidEmail;
import com.quiz.core.entities.validators.ValidUsername;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("unused")
@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class User
{
    @Id
    @GeneratedValue
    long id;

    @Column(nullable = false)
    @ValidUsername
    String username;

    @Column(nullable = false, unique = true)
    @ValidEmail
    String email;

    @Column(nullable = false)
    String passwordHash;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    RefreshToken refreshToken;

    public User(String username, String email, String passwordHash)
    {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }
}
