package com.example.noteagent.exception;

import org.springframework.http.HttpStatus;

public class UnsafePathException extends AppException {
    public UnsafePathException(String message) {
        super("UNSAFE_PATH", message, HttpStatus.BAD_REQUEST);
    }
}
