package com.quiz.services;

import com.quiz.core.entities.User;
import com.quiz.core.repositories.UserRepository;
import com.quiz.services.dtos.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultUserService implements UserService
{
    private final UserRepository userRepository;
    private final PasswordHashGenerator passwordHashGenerator;

    @Autowired
    public DefaultUserService(UserRepository userRepository,
                              PasswordHashGenerator passwordHashGenerator)
    {
        this.userRepository = userRepository;
        this.passwordHashGenerator = passwordHashGenerator;
    }

    @Override
    public void registerNewUser(UserDto userDto) throws UserAlreadyExistsException
    {
        if (userAlreadyExists(userDto))
        {
            throw new UserAlreadyExistsException(userDto.email());
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
