package com.wisestudent.exceptions.constraints;

import com.wisestudent.exceptions.validators.SexValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Констрейнт для пола человека
 */
@Target( {ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {SexValidator.class})
public @interface SexConstraint {
    String message() default "Указанного пола не существует.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
