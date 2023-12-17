package com.wisestudent.exceptions.constraints;

import com.wisestudent.exceptions.validators.CronFormatValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {CronFormatValidator.class})
public @interface CronFormat {
    String message() default "Крон выражение введено неверно, пример правильного: * * * * * ?";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
