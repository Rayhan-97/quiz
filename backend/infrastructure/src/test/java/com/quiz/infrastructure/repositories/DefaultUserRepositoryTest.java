package com.quiz.infrastructure.repositories;

import com.quiz.core.entities.User;
import com.quiz.core.repositories.UserRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ContextConfiguration(classes = DefaultUserRepository.class)
@EnableJpaRepositories(basePackageClasses = DefaultUserRepository.class)
@EntityScan(basePackages = "com.quiz")
class DefaultUserRepositoryTest
{
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void givenUserRepositoryHasUsers_whenFindByEmail_thenReturnUser()
    {
        String email = "email@email.com";
        User user = new User("username",email, "password hash");

        testEntityManager.persistAndFlush(user);

        Optional<User> optionalUser = userRepository.findByEmail(email);

        assertThat(optionalUser.isPresent()).isTrue();
        assertThat(optionalUser.get()).isEqualTo(user);
    }

    @Test
    void givenUserRepositoryHasNoUsers_whenFindByEmail_thenEmptyOptional()
    {
        String email = "email@email.com";
        Optional<User> optionalUser = userRepository.findByEmail(email);

        assertThat(optionalUser.isPresent()).isFalse();
    }

    @Test
    void givenUserRepositoryWithUserAndNewUserWithSameEmail_whenSave_thenError()
    {
        String email = "email@email.com";
        User user = new User("username", email, "passwordhash");
        User userWithDuplicateEmail = new User("username2", email, "passwrodhash2");

        testEntityManager.persistAndFlush(user);

        assertThatThrownBy(() -> testEntityManager.persistAndFlush(userWithDuplicateEmail))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("Unique index or primary key violation")
                .hasMessageContaining(email);
    }

    @Test
    void givenUserRepositoryAndNewUserWithInvalidUsername_whenSave_thenError()
    {
        String invalidUsername = "*^&bad-username";
        User user = new User(invalidUsername, "email@email.com", "passwordhash");

        assertThatThrownBy(() -> testEntityManager.persistAndFlush(user))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class)
                .hasMessageContaining("Invalid username");
    }

    @Test
    void givenUserRepositoryAndNewUserWithInvalidEmail_whenSave_thenError()
    {
        String invalidEmail = "a@a.a";
        User user = new User("username", invalidEmail, "passwordhash");

        assertThatThrownBy(() -> testEntityManager.persistAndFlush(user))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class)
                .hasMessageContaining("Invalid email");
    }
}