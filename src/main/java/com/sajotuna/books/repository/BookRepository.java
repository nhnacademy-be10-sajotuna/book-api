package com.sajotuna.books.repository;

import com.sajotuna.books.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String> { // Book 엔티티와 String 타입의 ID (ISBN)


    boolean existsByIsbn(String isbn);


}