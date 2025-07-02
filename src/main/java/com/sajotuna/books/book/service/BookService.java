package com.sajotuna.books.book.service;

import com.sajotuna.books.book.controller.request.BookCreateRequest;
import com.sajotuna.books.book.controller.response.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    Page<BookResponse> getAllBooks(Pageable pageable);

    BookResponse getBookByIsbn(String isbn);

    BookResponse createBook(BookCreateRequest request);

    // 도서 수정 기능 추가
    BookResponse updateBook(String isbn, BookCreateRequest request);

    // 도서 삭제 기능 추가
    void deleteBook(String isbn);
}