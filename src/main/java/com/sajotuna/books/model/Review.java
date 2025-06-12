package com.sajotuna.books.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_isbn", nullable = false)
    private Book book;

    @Column(nullable = false)
    private int rating; // 1 to 5

    @Lob
    private String comment;

    @Column(name = "review_date", nullable = false)
    private LocalDateTime reviewDate;

    public Review(User user, Book book, int rating, String comment) {
        this.user = user;
        this.book = book;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = LocalDateTime.now();
    }
}