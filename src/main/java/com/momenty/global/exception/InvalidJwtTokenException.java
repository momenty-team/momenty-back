package com.momenty.global.exception;

public class InvalidJwtTokenException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "유효하지 않은 토큰이거나 토큰이 없습니다.";

    public InvalidJwtTokenException(String message) {
        super(message);
    }

    public InvalidJwtTokenException(){
        super(DEFAULT_MESSAGE);
    }
}