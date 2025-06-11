package com.sajotuna.books.exception;

import org.springframework.http.HttpStatus;

public class BookNotFoundException extends ApiException {
    private static final String MESSAGE = "존재하지 않는 도서입니다: ";

    public BookNotFoundException(String isbn) {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE + isbn);
    }
}