package com.sajotuna.books.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404 Not Found
public class LikeNotFoundException extends RuntimeException {
    public LikeNotFoundException(Long userId, String bookIsbn) {
        super("Like not found for user ID: " + userId + " and book ISBN: " + bookIsbn);
    }
}