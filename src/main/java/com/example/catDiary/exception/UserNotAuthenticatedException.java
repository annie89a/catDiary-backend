package com.example.catDiary.exception;

public class UserNotAuthenticatedException extends RuntimeException {
    public UserNotAuthenticatedException() {
        super("User is not authenticated.");
    }
}
