package com.sajotuna.books.exception;

import org.springframework.http.HttpStatus;

public class DuplicateLikeException extends ApiException {
    private static final String MESSAGE = "이미 좋아요를 누른 도서입니다: %s";

    public DuplicateLikeException(Long userId, String bookIsbn) {
        super(HttpStatus.CONFLICT.value(), String.format(MESSAGE, userId, bookIsbn));
    }
}