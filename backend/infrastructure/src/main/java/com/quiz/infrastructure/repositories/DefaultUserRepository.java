package com.quiz.infrastructure.repositories;

import com.quiz.core.entities.User;
import com.quiz.core.repositories.UserRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefaultUserRepository extends UserRepository, CrudRepository<User, Long>
{
}
