package com.sajotuna.books.tag.exception;

import com.sajotuna.books.common.exception.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidTagNameException extends ApiException {
    private static final String MESSAGE = "유효하지 않은 태그 이름입니다.";

    public InvalidTagNameException() {
        super(HttpStatus.BAD_REQUEST.value(), MESSAGE);
    }
}