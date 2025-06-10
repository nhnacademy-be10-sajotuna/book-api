// src/main/java/com/sajotuna/books/dto/BookResponse.java
package com.sajotuna.books.dto;

import com.sajotuna.books.model.Book;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class BookResponse {
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private LocalDate publicationDate;
    private Integer pageCount;
    private String imageUrl;
    private String description;
    private String tableOfContents;
    private Double originalPrice;
    private Double sellingPrice;
    private Double discountRate;
    private Boolean giftWrappingAvailable;
    private Integer likes;
    private List<CategoryResponse> categories;
    private List<TagResponse> tags; // List<String>에서 List<TagResponse>로 변경

    public BookResponse(Book book) {
        this.isbn = book.getIsbn();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.publisher = book.getPublisher();
        this.publicationDate = book.getPublicationDate();
        this.pageCount = book.getPageCount();
        this.imageUrl = book.getImageUrl();
        this.description = book.getDescription();
        this.tableOfContents = book.getTableOfContents();
        this.originalPrice = book.getOriginalPrice();
        this.sellingPrice = book.getSellingPrice();
        this.discountRate = book.getDiscountRate();
        this.giftWrappingAvailable = book.getGiftWrappingAvailable();
        this.likes = book.getLikes();
        this.categories = book.getCategories().stream()
                .map(CategoryResponse::new)
                .collect(Collectors.toList());
        this.tags = book.getTags().stream() // Tag 엔티티를 TagResponse DTO로 변환
                .map(TagResponse::new)
                .collect(Collectors.toList());
    }
}