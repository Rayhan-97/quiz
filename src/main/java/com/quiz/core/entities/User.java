package com.quiz.core.entities;

import com.quiz.core.entities.validators.ValidEmail;
import com.quiz.core.entities.validators.ValidUsername;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("unused")
@Entity
@Getter
@Setter
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

    private User()
    {
        // no args jackson constructor
    }
    public User(String username, String email, String passwordHash)
    {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }
}
