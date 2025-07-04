// src/main/java/com/sajotuna/books/model/Like.java
package com.sajotuna.books.like.domain;

import com.sajotuna.books.book.domain.Book;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "likes") // 테이블 이름 명시
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_isbn", nullable = false) // Book 엔티티와 연결
    private Book book;

    public Like(Long userId, Book book) {
        this.userId = userId;
        this.book = book;
    }
}