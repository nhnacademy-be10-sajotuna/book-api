package com.sajotuna.books.tag.repository;

import com.sajotuna.books.book.domain.Book;
import com.sajotuna.books.tag.domain.BookTag;
import com.sajotuna.books.tag.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookTagRepository extends JpaRepository<BookTag, Long> {

    List<BookTag> findByBook(Book book);
    List<BookTag> findByTag(Tag tag);
}
