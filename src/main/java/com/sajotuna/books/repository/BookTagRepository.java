package com.sajotuna.books.repository;

import com.sajotuna.books.model.Book;
import com.sajotuna.books.model.BookTag;
import com.sajotuna.books.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookTagRepository extends JpaRepository<BookTag, Long> {

    List<BookTag> findByBook(Book book);
    List<BookTag> findByTag(Tag tag);
}
