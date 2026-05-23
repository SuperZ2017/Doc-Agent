package com.example.noteagent.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends AppException {
    public BadRequestException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.BAD_REQUEST);
    }
}
