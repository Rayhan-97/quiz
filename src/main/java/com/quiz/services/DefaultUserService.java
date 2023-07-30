package com.quiz.services;

import com.quiz.core.entities.NewUserDto;
import com.quiz.core.entities.User;
import com.quiz.core.repositories.UserRepository;
import com.quiz.core.validators.EmailValidator;
import com.quiz.core.validators.PasswordValidator;
import com.quiz.core.validators.UsernameValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultUserService implements UserService
{
    private final UserRepository userRepository;
    private final PasswordHashGenerator passwordHashGenerator;
    private final UsernameValidator usernameValidator;
    private final EmailValidator emailValidator;
    private final PasswordValidator passwordValidator;

    @Autowired
    public DefaultUserService(UserRepository userRepository,
                              PasswordHashGenerator passwordHashGenerator,
                              UsernameValidator usernameValidator,
                              PasswordValidator passwordValidator,
                              EmailValidator emailValidator)
    {
        this.userRepository = userRepository;
        this.passwordHashGenerator = passwordHashGenerator;
        this.usernameValidator = usernameValidator;
        this.passwordValidator = passwordValidator;
        this.emailValidator = emailValidator;
    }

    @Override
    public void registerNewUser(NewUserDto newUserDto) throws UserAlreadyExistsException
    {
        if (userAlreadyExists(newUserDto))
        {
            throw new UserAlreadyExistsException(newUserDto.email());
        }

        if (!usernameValidator.validate(newUserDto.username()))
        {
            throw new IllegalArgumentException();
        }
        if (!emailValidator.validate(newUserDto.email()))
        {
            throw new IllegalArgumentException();
        }
        passwordValidator.validate(newUserDto.password());

        String passwordHash = passwordHashGenerator.generateHash(newUserDto.password());
        User user = new User(newUserDto.username(), newUserDto.email(), passwordHash);

        userRepository.save(user);
    }

    private boolean userAlreadyExists(NewUserDto newUserDto)
    {
        return userRepository.findByEmail(newUserDto.email()).isPresent();
    }
}
