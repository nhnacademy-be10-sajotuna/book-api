// src/main/java/com/sajotuna/books/dto/BookRequest.java
package com.sajotuna.books.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class BookRequest {
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private LocalDate publicationDate;
    private Integer pageCount;
    private String imageUrl;
    private String description;
    private Double originalPrice;
    private Double sellingPrice;
    private Boolean giftWrappingAvailable;
    private Integer likes;
    private Set<Long> categoryIds;
    private Set<Long> tagIds; // Set<String>에서 Set<Long>으로 변경: 태그 ID 목록
}