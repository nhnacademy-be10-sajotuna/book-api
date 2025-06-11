package com.sajotuna.books.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus; // HttpStatus를 사용하기 위해 추가

@Getter
public class ApiException extends RuntimeException {
    private final int status;
    private final String message;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
}