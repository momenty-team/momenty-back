package com.momenty.global.exception;

public class AuthenticationException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "인증 에러";

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(){
        super(DEFAULT_MESSAGE);
    }
}
