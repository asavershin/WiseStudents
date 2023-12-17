package com.wisestudent.exceptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DuplicateException extends RuntimeException{
    private String fieldName;
    public DuplicateException(String message, String fieldName) {
        super(message);
        this.fieldName = fieldName;
    }

}
