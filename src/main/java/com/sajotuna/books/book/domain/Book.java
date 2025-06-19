package com.sajotuna.books.book.domain;

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

    private String title;
    private String author;
    private String publisher;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    // 추가: 페이지 수
    @Column(name = "page_count")
    private Integer pageCount;

    // 추가: 책 이미지 URL (대량의 바이너리 데이터 대신 URL로 대체)
    @Column(name = "image_url")
    private String imageUrl;

    // @Lob 어노테이션은 CLOB/BLOB 타입에 매핑됩니다.
    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "original_price")
    private Double originalPrice;

    @Column(name = "selling_price")
    private Double sellingPrice;

    @Column(name = "gift_wrapping_available")
    private Boolean giftWrappingAvailable;

    private Integer likes; // 좋아요 수

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookCategory> bookCategories = new HashSet<>();


    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookTag> bookTags = new HashSet<>();



    // 생성자 (필요에 따라 추가)
    public Book(String isbn, String title, String author, String publisher, LocalDate publicationDate,
                Integer pageCount, String imageUrl, String description,
                Double originalPrice, Double sellingPrice, Boolean giftWrappingAvailable, Integer likes) {
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
        this.giftWrappingAvailable = giftWrappingAvailable;
        this.likes = likes;
    }

    // 할인율 계산 getter (DTO에 포함될 수 있음)
    @Transient // 데이터베이스 컬럼으로 매핑하지 않음
    public Double getDiscountRate() {
        if (originalPrice != null && sellingPrice != null && originalPrice > 0) {
            return ((originalPrice - sellingPrice) / originalPrice) * 100.0;
        }
        return 0.0;
    }
}