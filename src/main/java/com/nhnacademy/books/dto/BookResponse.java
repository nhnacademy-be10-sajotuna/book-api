package com.nhnacademy.books.dto;

import com.nhnacademy.books.model.Book;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class BookResponse {
    private String isbn;
    private String title;
    private String tableOfContents;
    private String description;
    private Set<CategoryResponse> categories; // CategoryResponse DTO 사용
    private String author;
    private String publisher;
    private LocalDate publicationDate;
    private double originalPrice;
    private double sellingPrice;
    private double discountRate;
    private boolean giftWrappingAvailable;
    private Set<String> tags;
    private int likes;

    public BookResponse(Book book) {
        this.isbn = book.getIsbn();
        this.title = book.getTitle();
        this.tableOfContents = book.getTableOfContents();
        this.description = book.getDescription();
        // 카테고리도 DTO로 변환하여 순환 참조 방지
        this.categories = book.getCategories().stream()
                .map(CategoryResponse::new)
                .collect(Collectors.toSet());
        this.author = book.getAuthor();
        this.publisher = book.getPublisher();
        this.publicationDate = book.getPublicationDate();
        this.originalPrice = book.getOriginalPrice();
        this.sellingPrice = book.getSellingPrice();
        this.discountRate = book.getDiscountRate();
        this.giftWrappingAvailable = book.isGiftWrappingAvailable();
        this.tags = new HashSet<>(book.getTags()); // 태그는 그대로 전달
        this.likes = book.getLikes();
    }
}