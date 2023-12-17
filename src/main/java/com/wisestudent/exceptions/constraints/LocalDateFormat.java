package com.wisestudent.exceptions.constraints;

import com.wisestudent.exceptions.validators.LocalDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {LocalDateValidator.class})
public @interface LocalDateFormat {
    String message() default "Дата введена неверно, верный формат гггг-мм-дд";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
