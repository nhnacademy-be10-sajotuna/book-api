package com.sajotuna.books.category.exception;

import com.sajotuna.books.common.exception.ApiException;
import org.springframework.http.HttpStatus;

public class DuplicateCategoryException extends ApiException {
    private static final String MESSAGE = "동일한 이름의 카테고리가 이미 존재합니다: '%s'";
    private static final String MESSAGE_WITH_PARENT = "상위 카테고리 ID %d 하위에 동일한 이름의 카테고리가 이미 존재합니다: '%s'";

    public DuplicateCategoryException(String categoryName, Long parentCategoryId) {
        super(HttpStatus.CONFLICT.value(),
                parentCategoryId != null ?
                        String.format(MESSAGE_WITH_PARENT, parentCategoryId, categoryName) :
                        String.format(MESSAGE, categoryName));
    }
}