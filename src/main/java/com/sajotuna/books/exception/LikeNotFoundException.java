package com.sajotuna.books.exception;

import org.springframework.http.HttpStatus;

public class LikeNotFoundException extends ApiException {
    private static final String MESSAGE = "좋아요를 찾을 수 없습니다: %s";

    public LikeNotFoundException(Long userId, String bookIsbn) {
        super(HttpStatus.NOT_FOUND.value(), String.format(MESSAGE, userId, bookIsbn));
    }
}