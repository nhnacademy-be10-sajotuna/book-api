package com.sajotuna.books.book.repository;

import com.sajotuna.books.book.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // 추가
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, String>, JpaSpecificationExecutor<Book> { // JpaSpecificationExecutor 추가
    
    // 좋아요 많은 순서로 책 조회 (메인 배너용)
    Page<Book> findAllByOrderByLikesDesc(Pageable pageable);
}