package com.momenty.global.exception;

public class UserNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "해당 사용자를 찾을 수 없습니다.";

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
