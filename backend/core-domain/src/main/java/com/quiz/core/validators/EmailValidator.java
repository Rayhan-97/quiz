package com.quiz.core.validators;

import com.quiz.core.entities.validators.ValidEmail;
import jakarta.validation.ConstraintValidator;

public interface EmailValidator extends ConstraintValidator<ValidEmail, String>
{
    boolean validate(String email);
}
