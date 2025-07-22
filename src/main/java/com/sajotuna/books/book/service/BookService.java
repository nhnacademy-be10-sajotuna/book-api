package com.sajotuna.books.book.service;

import com.sajotuna.books.book.controller.request.BookBatchRequest;
import com.sajotuna.books.book.controller.request.BookCreateRequest;
import com.sajotuna.books.book.controller.response.BookResponse;
import com.sajotuna.books.book.controller.response.BookSummaryResponse;
import com.sajotuna.books.book.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification; // 추가

import java.util.List;

public interface BookService {
    Page<BookResponse> getAllBooks(Pageable pageable);
    // 검색 및 필터링을 위한 Specification 파라미터 추가
    Page<BookResponse> searchBooks(Specification<Book> spec, Pageable pageable);

    BookResponse createBook(BookCreateRequest request);

    BookResponse updateBook(String isbn, BookCreateRequest request);

    // 관리자 기능: 도서 재고 수정
    void updateBookStock(String isbn, Integer stock);

    // 관리자 기능: 도서 좋아요 수 수정
    BookResponse updateBookLikes(String isbn, Integer likes);

    BookResponse getBookByIsbn(String isbn); // 추가

    BookResponse getBookByIsbnByAdmin(String isbn);

    void updateReviewInfo(String isbn, double rating);

    // 도서 삭제 기능 추가
    void deleteBook(String isbn);

    List<BookSummaryResponse> getBooksByIsbns(BookBatchRequest request);
    
    // 좋아요 많은 순서로 책 조회 (메인 배너용)
    Page<BookResponse> getBooksByLikesDesc(Pageable pageable);
}