package com.sajotuna.books.category.exception;

import com.sajotuna.books.common.exception.ApiException;
import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends ApiException {
    private static final String MESSAGE = "존재하지 않는 카테고리입니다: ";

    public CategoryNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE + id);
    }
}