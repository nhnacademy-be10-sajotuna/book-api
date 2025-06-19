package com.sajotuna.books.tag.exception;

import com.sajotuna.books.common.exception.ApiException;
import org.springframework.http.HttpStatus;

public class TagAlreadyExistsException extends ApiException {
    private static final String MESSAGE = "이미 존재하는 태그입니다: ";

    public TagAlreadyExistsException(String tagName) {
        super(HttpStatus.CONFLICT.value(), MESSAGE + tagName);
    }

    public TagAlreadyExistsException(Long id, String tagName) {
        super(HttpStatus.CONFLICT.value(), MESSAGE + "[ID: " + id + ", 이름: " + tagName + "]");
    }
}