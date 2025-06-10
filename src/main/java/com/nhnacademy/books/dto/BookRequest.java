package com.nhnacademy.books.dto;

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
    private Integer pageCount; // 추가: 페이지 수
    private String imageUrl; // 추가: 이미지 URL
    private String description;
    private String tableOfContents;
    private Double originalPrice;
    private Double sellingPrice;
    private Boolean giftWrappingAvailable;
    private Integer likes;
    private Set<Long> categoryIds; // 책에 연결할 카테고리 ID 목록
    private Set<String> tags; // 책에 연결할 태그 목록
}