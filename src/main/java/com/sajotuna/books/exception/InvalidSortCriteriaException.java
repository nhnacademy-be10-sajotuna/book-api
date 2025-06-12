package com.sajotuna.books.exception;

import org.springframework.http.HttpStatus;

public class InvalidSortCriteriaException extends ApiException {
    private static final String MESSAGE = "유효하지 않은 정렬 기준입니다: ";

    public InvalidSortCriteriaException(String sortBy) {
        super(HttpStatus.BAD_REQUEST.value(), MESSAGE + sortBy);
    }
}