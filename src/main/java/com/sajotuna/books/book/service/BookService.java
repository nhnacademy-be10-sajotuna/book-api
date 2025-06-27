// src/main/java/com/sajotuna/books/book/service/BookService.java
package com.sajotuna.books.book.service;

import com.sajotuna.books.book.controller.request.BookCreateRequest;
import com.sajotuna.books.book.controller.response.BookResponse;
import org.springframework.data.domain.Page; // 추가
import org.springframework.data.domain.Pageable; // 추가

import java.util.List;

public interface BookService {
    Page<BookResponse> getAllBooks(Pageable pageable); // List -> Page, Pageable 추가

    BookResponse getBookByIsbn(String isbn);

    BookResponse createBook(BookCreateRequest request);
}