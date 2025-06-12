package com.sajotuna.books.exception;

// 필요한 경우 다른 import 문 추가

public class TagAlreadyExistsException extends RuntimeException {

    // 기존 생성자가 String message 하나만 받는 경우
    public TagAlreadyExistsException(String message) {
        super(message);
    }

    // ⭐ 이 생성자를 추가하거나 기존 생성자를 이 형태로 변경합니다. ⭐
    public TagAlreadyExistsException(Long tagId, String isbn) {
        super("Tag with ID " + tagId + " already exists for book with ISBN " + isbn);
    }
}