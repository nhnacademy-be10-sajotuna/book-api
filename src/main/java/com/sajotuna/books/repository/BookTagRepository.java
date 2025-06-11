package com.sajotuna.books.repository;

import com.sajotuna.books.model.BookTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookTagRepository extends JpaRepository<BookTag, Long> {
}
