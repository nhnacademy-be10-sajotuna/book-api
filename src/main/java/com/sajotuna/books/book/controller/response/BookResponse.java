package com.sajotuna.books.book.controller.response;

import com.sajotuna.books.category.controller.response.CategoryResponse;
import com.sajotuna.books.book.domain.Book;
import com.sajotuna.books.search.BookSearchDocument;
import com.sajotuna.books.search.repository.BookSearchRepository;
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
    private Integer pageCount;
    private String imageUrl;
    private String description;
    private Double sellingPrice;
    private Double originalPrice;
    private Double discountRate;
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
        this.pageCount = book.getPageCount();
        this.imageUrl = book.getImageUrl();
        this.description = book.getDescription();
        this.sellingPrice = book.getSellingPrice();
        this.originalPrice = book.getOriginalPrice();
        this.discountRate = book.getDiscountRate();
        this.giftWrappingAvailable = book.getGiftWrappingAvailable();
        this.likes = book.getLikes();
        
        // 기본값 설정 (ES 조회 실패 시)
        this.averageRating = 0.0;
        this.reviewCount = 0;
        this.viewCount = 0;

        this.categories = extractCategoryPath(book);

        this.tags = book.getBookTags().stream()
                .map(BookTag::getTag)
                .map(Tag::getTagName)
                .collect(Collectors.toSet());
    }
    
    // ES에서 통계 정보를 설정하는 생성자
    public BookResponse(Book book, BookSearchRepository bookSearchRepository) {
        this(book); // 기본 생성자 호출
        
        // ES에서 통계 조회
        try {
            BookSearchDocument stats = bookSearchRepository.findById(book.getIsbn()).orElse(null);
            if (stats != null) {
                this.averageRating = stats.getAverageRating();
                this.reviewCount = stats.getReviewCount();
                this.viewCount = stats.getViewCount();
            }
        } catch (Exception e) {
            // ES 조회 실패 시 기본값 유지 (이미 설정됨)
        }
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