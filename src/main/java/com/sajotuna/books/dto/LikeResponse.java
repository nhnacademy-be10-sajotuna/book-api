// src/main/java/com/sajotuna/books/dto/LikeResponse.java
package com.sajotuna.books.dto;

import com.sajotuna.books.model.Like;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LikeResponse {
    private Long id;
    private Long userId;
    private String bookIsbn;
    private String bookTitle; // 책 제목도 포함

    public LikeResponse(Like like) {
        this.id = like.getId();
        this.userId = like.getUserId();
        this.bookIsbn = like.getBook().getIsbn();
        this.bookTitle = like.getBook().getTitle();
    }
}