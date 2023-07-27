package com.quiz.core.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class User
{
    @Id
    @GeneratedValue
    long id;

    @Column(nullable = false)
    String username;

    @Column(nullable = false)
    String email;

    @Column(nullable = false)
    String passwordHash;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPasswordHash()
    {
        return passwordHash;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        User user = (User) o;
        return id == user.id && Objects.equals(username, user.username) && Objects.equals(email, user.email) && Objects.equals(passwordHash, user.passwordHash);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, username, email, passwordHash);
    }

    public void setPasswordHash(String passwordHash)
    {
        this.passwordHash = passwordHash;
    }
}
