package com.quiz.core.validators;

import com.quiz.core.entities.validators.ValidUsername;
import jakarta.validation.ConstraintValidator;

public interface UsernameValidator extends ConstraintValidator<ValidUsername, String>
{
    boolean validate(String username);
}
