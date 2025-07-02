package com.sajotuna.books.book.repository;

import com.sajotuna.books.book.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // 추가
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, String>, JpaSpecificationExecutor<Book> { // JpaSpecificationExecutor 추가
}