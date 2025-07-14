package com.sajotuna.books.tag.domain;

import com.sajotuna.books.book.domain.Book;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookTag)) return false;
        BookTag that = (BookTag) o;
        return Objects.equals(book.getIsbn(), that.book.getIsbn()) &&
                Objects.equals(tag.getId(), that.tag.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(book.getIsbn(), tag.getId());
    }


}
