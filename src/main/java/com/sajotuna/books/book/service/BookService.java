package com.sajotuna.books.book.service;

import com.sajotuna.books.book.controller.request.BookCreateRequest;
import com.sajotuna.books.book.controller.response.BookResponse;
import com.sajotuna.books.book.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification; // 추가

public interface BookService {
    Page<BookResponse> getAllBooks(Pageable pageable);
    // 검색 및 필터링을 위한 Specification 파라미터 추가
    Page<BookResponse> searchBooks(Specification<Book> spec, Pageable pageable);

    BookResponse getBookByIsbn(String isbn);

    BookResponse createBook(BookCreateRequest request);

    BookResponse updateBook(String isbn, BookCreateRequest request);

    // 관리자 기능: 도서 재고 수정
    BookResponse updateBookStock(String isbn, Integer stock);

    // 관리자 기능: 도서 좋아요 수 수정
    BookResponse updateBookLikes(String isbn, Integer likes);

    BookResponse getBookByIsbn(String isbn); // 추가

    void updateReviewInfo(String isbn, double rating);

    // 도서 삭제 기능 추가
    void deleteBook(String isbn);
}