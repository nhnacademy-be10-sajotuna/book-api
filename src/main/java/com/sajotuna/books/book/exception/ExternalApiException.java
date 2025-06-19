package com.sajotuna.books.book.exception;

import com.sajotuna.books.common.exception.ApiException;
import org.springframework.http.HttpStatus;

public class ExternalApiException extends ApiException {

    private static final String MESSAGE = "외부 API 오류입니다";

    public ExternalApiException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), MESSAGE);
    }
}
