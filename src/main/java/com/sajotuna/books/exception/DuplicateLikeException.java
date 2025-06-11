package com.sajotuna.books.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // 409 Conflict
public class DuplicateLikeException extends RuntimeException {
    public DuplicateLikeException(Long userId, String bookIsbn) {
        super("User with ID: " + userId + " has already liked the book with ISBN: " + bookIsbn);
    }
}