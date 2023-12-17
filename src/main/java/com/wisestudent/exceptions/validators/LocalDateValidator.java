package com.wisestudent.exceptions.validators;

import com.wisestudent.exceptions.constraints.LocalDateFormat;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class LocalDateValidator implements ConstraintValidator<LocalDateFormat, String> {

    @Override
    public boolean isValid(String date, ConstraintValidatorContext constraintValidatorContext) {
        try {
            LocalDate.parse(date);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
