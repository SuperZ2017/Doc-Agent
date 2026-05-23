package com.example.noteagent.exception;

import org.springframework.http.HttpStatus;

public class TaskConflictException extends AppException {
    public TaskConflictException(String message) {
        super("TASK_CONFLICT", message, HttpStatus.CONFLICT);
    }
}
