package com.quiz.core.validators;

import com.quiz.core.entities.validators.ValidPassword;
import jakarta.validation.ConstraintValidator;

public interface PasswordValidator extends ConstraintValidator<ValidPassword, String>
{
    boolean validate(String password);
}
