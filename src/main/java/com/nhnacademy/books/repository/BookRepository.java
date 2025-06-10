package com.nhnacademy.books.repository;

import com.nhnacademy.books.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, String> { // Book 엔티티와 String 타입의 ID (ISBN)
    // Spring Data JPA의 메소드 쿼리 기능을 활용
    List<Book> findByTitleContainingIgnoreCase(String titleKeyword);
    List<Book> findByAuthorContainingIgnoreCase(String authorKeyword);

    // ISBN으로 조회는 JpaRepository 기본 메서드인 findById(String id) 사용 가능
    // Optional<Book> findByIsbn(String isbn); // 직접 정의할 필요 없음
}