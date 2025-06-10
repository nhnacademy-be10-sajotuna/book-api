package com.nhnacademy.books.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Book {
    @Id
    @Column(unique = true, nullable = false)
    private String isbn; // ISBN을 기본 키로 사용

    @Column(nullable = false)
    private String title;

    @Lob // CLOB 타입으로 매핑 (긴 문자열)
    private String tableOfContents;

    @Lob // CLOB 타입으로 매핑 (긴 문자열)
    private String description;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}) // Book 저장/업데이트 시 Category도 연동
    @JoinTable(
            name = "book_categories",
            joinColumns = @JoinColumn(name = "book_isbn"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>(); // Set을 사용하여 중복 방지

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private LocalDate publicationDate;

    @Column(nullable = false)
    private double originalPrice;

    @Column(nullable = false)
    private double sellingPrice;

    @Column(nullable = false)
    private boolean giftWrappingAvailable;

    @ElementCollection // 컬렉션 타입의 속성 (별도 테이블로 저장)
    @CollectionTable(name = "book_tags", joinColumns = @JoinColumn(name = "book_isbn"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    private int likes; // 좋아요 수 (사용자 좋아요 기능 구현 시 별도 테이블로 관리하는 것이 좋음)

    public Book(String isbn, String title, String author, String publisher, LocalDate publicationDate, double originalPrice, double sellingPrice) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationDate = publicationDate;
        this.originalPrice = originalPrice;
        this.sellingPrice = sellingPrice;
        this.giftWrappingAvailable = false;
        this.likes = 0;
    }

    public double getDiscountRate() {
        if (originalPrice <= 0) {
            return 0.0;
        }
        return ((originalPrice - sellingPrice) / originalPrice) * 100;
    }

    // 카테고리 추가 편의 메서드
    public void addCategory(Category category) {
        if (this.categories.size() < 10) {
            this.categories.add(category);
            category.getBooks().add(this); // Category 쪽에서도 Book을 추가 (양방향 관계 관리)
        } else {
            System.out.println("경고: 도서는 최대 10개의 카테고리에만 속할 수 있습니다.");
        }
    }

    // 카테고리 제거 편의 메서드
    public void removeCategory(Category category) {
        this.categories.remove(category);
        category.getBooks().remove(this);
    }

    // 태그 추가 편의 메서드
    public void addTag(String tag) {
        this.tags.add(tag);
    }

    // 좋아요 수 증가
    public void incrementLikes() {
        this.likes++;
    }

    // 좋아요 수 감소
    public void decrementLikes() {
        if (this.likes > 0) {
            this.likes--;
        }
    }
}