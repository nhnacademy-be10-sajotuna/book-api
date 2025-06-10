// src/main/java/com/sajotuna/books/model/Book.java
package com.sajotuna.books.model;

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
@Table(name = "books")
public class Book {

    @Id
    private String isbn;

    private String title;
    private String author;
    private String publisher;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "image_url")
    private String imageUrl;

    @Lob
    @Column(name = "description")
    private String description;

    @Lob
    @Column(name = "table_of_contents")
    private String tableOfContents;

    @Column(name = "original_price")
    private Double originalPrice;

    @Column(name = "selling_price")
    private Double sellingPrice;

    @Column(name = "gift_wrapping_available")
    private Boolean giftWrappingAvailable;

    private Integer likes;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_categories",
            joinColumns = @JoinColumn(name = "book_isbn"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    // 기존의 @ElementCollection 대신 @ManyToMany 관계로 변경
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_tags_join", // 새로운 조인 테이블 이름 (기존 book_tags와 구분)
            joinColumns = @JoinColumn(name = "book_isbn"),
            inverseJoinColumns = @JoinColumn(name = "tag_id") // Tag 엔티티의 ID와 연결
    )
    private Set<Tag> tags = new HashSet<>(); // Set<String>에서 Set<Tag>로 변경

    // 생성자 (Tag 타입 변경 반영)
    public Book(String isbn, String title, String author, String publisher, LocalDate publicationDate,
                Integer pageCount, String imageUrl, String description, String tableOfContents,
                Double originalPrice, Double sellingPrice, Boolean giftWrappingAvailable, Integer likes) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationDate = publicationDate;
        this.pageCount = pageCount;
        this.imageUrl = imageUrl;
        this.description = description;
        this.tableOfContents = tableOfContents;
        this.originalPrice = originalPrice;
        this.sellingPrice = sellingPrice;
        this.giftWrappingAvailable = giftWrappingAvailable;
        this.likes = likes;
    }

    @Transient
    public Double getDiscountRate() {
        if (originalPrice != null && sellingPrice != null && originalPrice > 0) {
            return ((originalPrice - sellingPrice) / originalPrice) * 100.0;
        }
        return 0.0;
    }
}