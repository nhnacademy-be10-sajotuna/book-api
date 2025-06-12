package com.sajotuna.books.repository;

import com.sajotuna.books.model.Book;
import com.sajotuna.books.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBook(Book book);
    long countByBook(Book book);
}