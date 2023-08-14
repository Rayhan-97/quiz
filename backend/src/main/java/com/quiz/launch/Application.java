package com.quiz.launch;

import com.quiz.core.repositories.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.quiz"})
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
@EntityScan(basePackages = "com.quiz")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
