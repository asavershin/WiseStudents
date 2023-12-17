package com.wisestudent.exceptions.validators;

import com.wisestudent.exceptions.constraints.SexConstraint;
import com.wisestudent.models.Sex;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SexValidator implements ConstraintValidator<SexConstraint, String> {

    @Override
    public boolean isValid(String sexRaw, ConstraintValidatorContext constraintValidatorContext) {
        try {
            Sex.valueOf(sexRaw);
        } catch (Exception e) {
            String message = "Указанного пола не существует. Возможные значения: " +
                    Arrays.stream(Sex.values()).map(Object::toString).collect(Collectors.joining(", "));

            constraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        return true;
    }
}
