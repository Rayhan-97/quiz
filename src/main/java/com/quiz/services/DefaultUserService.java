package com.quiz.services;

import com.quiz.core.entities.User;
import com.quiz.core.repositories.UserRepository;
import com.quiz.core.validators.EmailValidator;
import com.quiz.core.validators.PasswordValidator;
import com.quiz.core.validators.UsernameValidator;
import com.quiz.services.dtos.UserDto;
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
    public void registerNewUser(UserDto userDto) throws UserAlreadyExistsException
    {
        if (userAlreadyExists(userDto))
        {
            throw new UserAlreadyExistsException(userDto.email());
        }

        if (!usernameValidator.validate(userDto.username()))
        {
            throw new IllegalArgumentException();
        }
        if (!emailValidator.validate(userDto.email()))
        {
            throw new IllegalArgumentException();
        }
        if (!passwordValidator.validate(userDto.password()))
        {
            throw new IllegalArgumentException();
        }

        String passwordHash = passwordHashGenerator.generateHash(userDto.password());
        User user = new User(userDto.username(), userDto.email(), passwordHash);

        userRepository.save(user);
    }

    private boolean userAlreadyExists(UserDto userDto)
    {
        return userRepository.findByEmail(userDto.email()).isPresent();
    }
}
