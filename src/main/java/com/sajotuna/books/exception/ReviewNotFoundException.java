package com.sajotuna.books.exception;

import org.springframework.http.HttpStatus;

public class ReviewNotFoundException extends ApiException {
    private static final String MESSAGE = "존재하지 않는 리뷰입니다: ";

    public ReviewNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE + id);
    }
}