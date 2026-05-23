package com.example.noteagent.exception;

import org.springframework.http.HttpStatus;

public class AgentInvocationException extends AppException {
    public AgentInvocationException(String message) {
        super("AGENT_INVOCATION_FAILED", message, HttpStatus.BAD_GATEWAY);
    }

    public AgentInvocationException(String message, Throwable cause) {
        super("AGENT_INVOCATION_FAILED", message, HttpStatus.BAD_GATEWAY, cause);
    }
}
