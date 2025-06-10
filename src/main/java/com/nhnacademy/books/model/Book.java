package com.nhnacademy.books.model;

import com.fasterxml.jackson.annotation.JsonManagedReference; // 추가
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
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Lob
    private String tableOfContents;

    @Lob
    private String description;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "book_categories",
            joinColumns = @JoinColumn(name = "book_isbn"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @JsonManagedReference // !!!!!!!! 이 부분 추가 !!!!!!!!
    private Set<Category> categories = new HashSet<>();

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

    @ElementCollection
    @CollectionTable(name = "book_tags", joinColumns = @JoinColumn(name = "book_isbn"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    private int likes;

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

    public void addCategory(Category category) {
        if (this.categories.size() < 10) {
            this.categories.add(category);
            category.getBooks().add(this);
        } else {
            System.out.println("경고: 도서는 최대 10개의 카테고리에만 속할 수 있습니다.");
        }
    }

    public void removeCategory(Category category) {
        this.categories.remove(category);
        category.getBooks().remove(this);
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public void incrementLikes() {
        this.likes++;
    }

    public void decrementLikes() {
        if (this.likes > 0) {
            this.likes--;
        }
    }
}