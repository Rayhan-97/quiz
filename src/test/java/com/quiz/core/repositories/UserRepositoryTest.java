package com.quiz.core.repositories;

import com.quiz.core.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ContextConfiguration(classes = UserRepository.class)
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
@EntityScan(basePackages = "com.quiz")
class UserRepositoryTest
{
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void givenUserRepositoryHasUsers_whenFindByEmail_thenReturnUser()
    {
        User user = new User();
        user.setUsername("username");
        String email = "email@email.com";
        user.setEmail(email);
        user.setPasswordHash("password hash");

        testEntityManager.persist(user);
        testEntityManager.flush();

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
}