package com.sajotuna.books.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApiException {
    private static final String MESSAGE = "존재하지 않는 유저입니다: ";

    public UserNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE + id);
    }
}