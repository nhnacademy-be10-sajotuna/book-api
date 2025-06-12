package com.sajotuna.books.dto;

import com.sajotuna.books.model.BookTag;

    public record BookTagResponse(Long id, Long tagId, String tagName, String isbn) {
        public static BookTagResponse from(BookTag bookTag) {
            return new BookTagResponse(
                    bookTag.getId(),
                    bookTag.getTag().getId(),
                    bookTag.getTag().getTagName(),
                    bookTag.getBook().getIsbn()
            );
        }
    }
