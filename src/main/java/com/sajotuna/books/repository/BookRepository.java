package com.sajotuna.books.repository;

import com.sajotuna.books.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    List<Book> findByTitleContainingIgnoreCase(String titleKeyword);
    List<Book> findByAuthorContainingIgnoreCase(String authorKeyword);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.categories LEFT JOIN FETCH b.bookTags bt LEFT JOIN FETCH bt.tag WHERE b.isbn = :isbn")
    Optional<Book> findByIdWithCategoriesAndTags(@Param("isbn") String isbn);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.categories LEFT JOIN FETCH b.bookTags bt LEFT JOIN FETCH bt.tag")
    List<Book> findAllWithCategoriesAndTags();

    // ⭐ 이 findByKeyword 메서드를 삭제합니다. ⭐
    // @Query("SELECT b FROM Book b WHERE " +
    //         "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
    //         "LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
    //         "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    // List<Book> findByKeyword(@Param("keyword") String keyword);
}