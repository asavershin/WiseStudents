package com.wisestudent.exceptions.validators;

import com.wisestudent.exceptions.constraints.CronFormat;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.quartz.CronExpression;

import java.text.ParseException;

public class CronFormatValidator implements ConstraintValidator<CronFormat, String> {
    @Override
    public boolean isValid(String cronExpression, ConstraintValidatorContext constraintValidatorContext) {
        try {
            new CronExpression(cronExpression);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
