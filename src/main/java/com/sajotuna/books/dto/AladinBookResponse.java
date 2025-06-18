package com.sajotuna.books.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AladinBookResponse {
    private String title;
    private String author;
    private String pubDate;
    private String description;
    private String isbn;
    private String isbn13;
    private Long itemId;
    private Integer priceSales;
    private Integer priceStandard;
    private String cover;
    private String publisher;
    private String categoryName;
}
