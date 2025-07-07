package com.sajotuna.books.category.exception;

import com.sajotuna.books.common.exception.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidCategoryIdFormatException extends ApiException {
    private static final String MESSAGE = "카테고리 ID 형식이 잘못되었습니다";

    public InvalidCategoryIdFormatException(String categoryId) {
      super(HttpStatus.BAD_REQUEST.value(), MESSAGE + categoryId);
    }
}
