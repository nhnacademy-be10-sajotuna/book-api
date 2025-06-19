package com.sajotuna.books.dto;

import com.sajotuna.books.model.Book;
import com.sajotuna.books.model.BookCategory;
import com.sajotuna.books.model.BookTag;
import com.sajotuna.books.model.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
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
    private Integer pageCount; // 추가: 페이지 수
    private String imageUrl; // 추가: 이미지 URL
    private String description;
    private Double originalPrice;
    private Double sellingPrice;
    private Double discountRate; // 할인율은 계산된 값
    private Boolean giftWrappingAvailable;
    private Integer likes;
    private List<CategoryResponse> categories; // 책에 연결된 카테고리
    private Set<String> tags; // 책에 연결된 태그

    public BookResponse(Book book) {
        this.isbn = book.getIsbn();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.publisher = book.getPublisher();
        this.publicationDate = book.getPublicationDate();
        this.pageCount = book.getPageCount(); // 추가
        this.imageUrl = book.getImageUrl(); // 추가
        this.description = book.getDescription();
        this.originalPrice = book.getOriginalPrice();
        this.sellingPrice = book.getSellingPrice();
        this.discountRate = book.getDiscountRate(); // 계산된 값 사용
        this.giftWrappingAvailable = book.getGiftWrappingAvailable();
        this.likes = book.getLikes();
        this.categories = book.getBookCategories().stream()
                .map(BookCategory::getCategory)
                .map(CategoryResponse::new)
                .collect(Collectors.toList());

        this.tags = book.getBookTags().stream()
                .map(BookTag::getTag)
                .map(Tag::getTagName)
                .collect(Collectors.toSet());

    }
}