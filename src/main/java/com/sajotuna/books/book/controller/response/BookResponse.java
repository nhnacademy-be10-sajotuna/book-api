package com.sajotuna.books.book.controller.response;

import com.sajotuna.books.category.controller.response.CategoryResponse;
import com.sajotuna.books.book.domain.Book;
import com.sajotuna.books.tag.domain.BookTag;
import com.sajotuna.books.tag.domain.Tag;
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
    private List<List<CategoryResponse>> categories;
    private Set<String> tags; // 책에 연결된 태그
    private Double averageRating;
    private int reviewCount;
    private int viewCount;

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
        this.averageRating = book.getAverageRating();
        this.reviewCount = book.getReviewCount();
        this.viewCount = book.getViewCount();

        this.categories = extractCategoryPath(book);

        this.tags = book.getBookTags().stream()
                .map(BookTag::getTag)
                .map(Tag::getTagName)
                .collect(Collectors.toSet());


    }

    private static List<List<CategoryResponse>> extractCategoryPath(Book book) {
        return book.getBookCategories().stream()
                .map(b -> b.getCategory().getPathFromRoot())
                .map(categories -> categories.stream()
                        .map(CategoryResponse::new)
                        .toList())
                .toList();
    }


}