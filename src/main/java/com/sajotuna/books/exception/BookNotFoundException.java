package com.sajotuna.books.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404 Not Found
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String isbn) {
        super("Book not found with ISBN: " + isbn);
    }
}