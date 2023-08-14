package com.quiz.core.repositories;

import com.quiz.core.entities.User;

import java.util.Optional;

public interface UserRepository
{
    Optional<User> findByEmail(String email);

    <S extends User> S save(S entity);

    Iterable<User> findAll();
}
