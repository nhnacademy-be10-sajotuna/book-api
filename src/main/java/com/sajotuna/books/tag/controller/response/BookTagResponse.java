package com.sajotuna.books.tag.controller.response;

import com.sajotuna.books.tag.domain.BookTag;

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
