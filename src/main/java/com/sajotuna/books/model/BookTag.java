package com.sajotuna.books.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "book_tags", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tag_id", "isbn"})
})
public class BookTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")//책 - 태그 아이디
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @ManyToOne
    @JoinColumn(name = "isbn")
    private Book book;

    public BookTag(Tag tag, Book book) {
        this.tag = tag;
        this.book = book;
    }



}
