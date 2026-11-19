package com.sieve.reminder.service;

public class TaskQueryUnavailableException extends RuntimeException {

    public TaskQueryUnavailableException(String message) {
        super(message);
    }

    public TaskQueryUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
