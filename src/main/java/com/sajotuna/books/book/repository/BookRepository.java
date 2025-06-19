package com.sajotuna.books.book.repository;

import com.sajotuna.books.book.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, String> { // Book 엔티티와 String 타입의 ID (ISBN)


    boolean existsByIsbn(String isbn);


}