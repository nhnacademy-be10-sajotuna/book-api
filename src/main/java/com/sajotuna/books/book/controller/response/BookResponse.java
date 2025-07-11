package com.sajotuna.books.book.controller.response;

import com.sajotuna.books.book.domain.Book;
import com.sajotuna.books.category.controller.response.CategoryResponse; // 임포트 추가
import com.sajotuna.books.category.domain.Category; // 임포트 추가
import com.sajotuna.books.tag.controller.response.TagResponse; // 임포트 추가
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
    private Double originalPrice;
    private Double sellingPrice;
    private Double discountRate;
    private Boolean giftWrappingAvailable;
    private Integer likes;
    private List<List<CategoryResponse>> categories; // 계층 구조를 위해 List<List<CategoryResponse>>로 변경
    private List<TagResponse> tags;
    private double averageRating;
    private int reviewCount;
    private int viewCount;

    // --- 추가된 부분 시작 ---
    private Integer stock; // stock 필드 추가
    // --- 추가된 부분 끝 ---

    public BookResponse(Book book) {
        this.isbn = book.getIsbn();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.publisher = book.getPublisher();
        this.publicationDate = book.getPublicationDate();
        this.pageCount = book.getPageCount();
        this.imageUrl = book.getImageUrl();
        this.description = book.getDescription();
        this.originalPrice = book.getOriginalPrice();
        this.sellingPrice = book.getSellingPrice();
        this.discountRate = book.getDiscountRate();
        this.giftWrappingAvailable = book.getGiftWrappingAvailable();
        this.likes = book.getLikes();
        this.averageRating = book.getAverageRating();
        this.reviewCount = book.getReviewCount();
        this.viewCount = book.getViewCount();

        // 카테고리 계층 구조 매핑
        this.categories = book.getBookCategories().stream()
                .map(bookCategory -> bookCategory.getCategory().getPathFromRoot().stream()
                        .map(CategoryResponse::new)
                        .toList())
                .collect(Collectors.toList());

        // 태그 매핑
        this.tags = book.getBookTags().stream()
                .map(bookTag -> TagResponse.from(bookTag.getTag()))
                .collect(Collectors.toList());

        // --- 추가된 부분 시작 ---
        this.stock = book.getStock(); // book 엔티티에서 stock 값을 가져와 설정
        // --- 추가된 부분 끝 ---
    }
}