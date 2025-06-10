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
@Table(name = "books") // 테이블 이름 명시
public class Book {
    @Id
    @Column(name = "isbn", length = 20, nullable = false)
    private String isbn; // 국제 표준 도서 번호

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "book_index", nullable = false)
    private String bookIndex; // 목차

    // @Lob 어노테이션은 CLOB/BLOB 타입에 매핑됩니다.
    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "publisher", nullable = false)
    private String publisher;

    @Column(name = "issued_at", nullable = false)
    private LocalDate issuedAt;

    @Column(name = "page", nullable = false)
    private int page;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "regular_price", nullable = false)
    private int regularPrice;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    // Book 엔티티와 Category 엔티티 간의 다대다 관계
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_categories",
            joinColumns = @JoinColumn(name = "book_isbn"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    // Book 엔티티와 Tag (단순 문자열) 간의 관계
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_tags", joinColumns = @JoinColumn(name = "book_isbn"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();
}