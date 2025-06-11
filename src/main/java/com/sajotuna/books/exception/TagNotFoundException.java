package com.sajotuna.books.exception;

import org.springframework.http.HttpStatus;

public class TagNotFoundException extends ApiException {

    private static final String MESSAGE = "존재하지 않는 태그입니다: ";

    public TagNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE + id);
    }

    public TagNotFoundException(String tagName) {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE + tagName);
    }
}
