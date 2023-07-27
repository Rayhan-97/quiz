package com.quiz.services;

import com.quiz.core.entities.NewUserDto;
import com.quiz.core.entities.User;
import com.quiz.core.repositories.UserRepository;
import com.quiz.core.validators.EmailValidator;
import com.quiz.core.validators.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultUserService implements UserService
{
    private final UserRepository userRepository;
    private final PasswordHashGenerator passwordHashGenerator;
    private final PasswordValidator passwordValidator;
    private final EmailValidator emailValidator;

    @Autowired
    public DefaultUserService(UserRepository userRepository,
                              PasswordHashGenerator passwordHashGenerator,
                              PasswordValidator passwordValidator,
                              EmailValidator emailValidator)
    {
        this.userRepository = userRepository;
        this.passwordHashGenerator = passwordHashGenerator;
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

        passwordValidator.validate(newUserDto.password());
        emailValidator.validate(newUserDto.email());

        User user = new User();
        user.setUsername(newUserDto.username());
        user.setEmail(newUserDto.email());
        String passwordHash = passwordHashGenerator.generateHash(newUserDto.password());
        user.setPasswordHash(passwordHash);

        userRepository.save(user);
    }

    private boolean userAlreadyExists(NewUserDto newUserDto)
    {
        return userRepository.findByEmail(newUserDto.email()).isPresent();
    }
}
