package com.sajotuna.books.book.domain;

import com.sajotuna.books.book.controller.request.BookCreateRequest;
import com.sajotuna.books.category.domain.BookCategory;
import com.sajotuna.books.tag.domain.BookTag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "books") // 테이블 이름 명시
public class Book {

    @Id
    private String isbn; // 국제 표준 도서 번호

    @Column(length = 1000, nullable = false)
    private String title;

    @Column(length = 1000, nullable = false)
    private String author;

    @Column(length = 1000, nullable = false)
    private String publisher;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    private Integer pageCount;

    // 추가: 책 이미지 URL (대량의 바이너리 데이터 대신 URL로 대체)
    @Column(length = 1024)
    private String imageUrl;

    @Lob
    private String description;

    private Double originalPrice;

    private Double sellingPrice;

    @Column(name = "stock") // 재고 필드 추가
    private Integer stock;

    private Boolean giftWrappingAvailable;

    private Integer likes; // 좋아요 수

    @Column(nullable = false)//
    private int viewCount;

    private double averageRating;

    private int reviewCount;

    private int searchCount;

    private double popularity;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookCategory> bookCategories = new HashSet<>();


    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookTag> bookTags = new HashSet<>();

    // 생성자 (필요에 따라 추가)
    public Book(String isbn, String title, String author, String publisher, LocalDate publicationDate,
                Integer pageCount, String imageUrl, String description,
                Double originalPrice, Double sellingPrice, Integer stock, Boolean giftWrappingAvailable, Integer likes) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationDate = publicationDate;
        this.pageCount = pageCount;
        this.imageUrl = imageUrl;
        this.description = description;
        this.originalPrice = originalPrice;
        this.sellingPrice = sellingPrice;
        this.stock = stock; // 생성자에 재고 필드 추가
        this.giftWrappingAvailable = giftWrappingAvailable;
        this.likes = likes;
        this.viewCount = 0;
        this.averageRating = 0.0;
        this.reviewCount = 0;
        this.popularity = 0.0;
    }

    // 할인율 계산 getter (DTO에 포함될 수 있음)
    @Transient // 데이터베이스 컬럼으로 매핑하지 않음
    public Double getDiscountRate() {
        if (originalPrice != null && sellingPrice != null && originalPrice > 0) {
            return ((originalPrice - sellingPrice) / originalPrice) * 100.0;
        }
        return 0.0;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void incrementReviewCount() {
        this.reviewCount++;
    }

    public void calculateRating(double rating) {
        averageRating = ((averageRating*reviewCount) + rating)/ (reviewCount + 1);

        averageRating = Math.round(averageRating * 10.0) / 10.0;
    }

    public void incrementSearchCount() {
        this.searchCount++;
    }

    public void calculatePopularity() {
        this.popularity = viewCount * 0.7 + searchCount * 0.3;
    }

    // 도서 정보 업데이트 메서드 (추가된 부분)
    public void updateInfo(BookCreateRequest request) {
        this.title = request.getTitle();
        this.author = request.getAuthor();
        this.publisher = request.getPublisher();
        this.publicationDate = request.getPublicationDate();
        this.pageCount = request.getPageCount();
        this.imageUrl = request.getImageUrl();
        this.description = request.getDescription();
        this.originalPrice = request.getOriginalPrice();
        this.sellingPrice = request.getSellingPrice();
        this.stock = request.getStock(); // 재고 업데이트 추가
        this.giftWrappingAvailable = request.getGiftWrappingAvailable();
        // 좋아요 수는 업데이트 시 변경하지 않음 (따로 관리)
    }

    // 좋아요 수 업데이트 메서드 추가 (관리자 기능)
    public void updateLikes(Integer newLikes) {
        this.likes = newLikes;
    }

    // 재고 수 업데이트 메서드 추가 (관리자 기능)
    public void updateStock(Integer newStock) {
        this.stock = newStock;
    }
}