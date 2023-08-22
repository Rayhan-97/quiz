package com.quiz.services;

import com.quiz.services.dtos.UserCredentialsDto;

public interface LoginService
{
    String retrieveLoginToken(UserCredentialsDto userCredentialsDto);
}
