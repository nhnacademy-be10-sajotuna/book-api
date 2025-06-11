package com.sajotuna.books.exception;

import org.springframework.http.HttpStatus;

public class TagAlreadyExistsException extends ApiException{
    private static final String MESSAGE = "이미 존재하는 태그입니다: ";

    public TagAlreadyExistsException(String tagName) {
        super(HttpStatus.CONFLICT.value(), MESSAGE + tagName);
    }
}