package com.sajotuna.books.service;

import com.sajotuna.books.dto.BookRequest;
import com.sajotuna.books.dto.BookResponse;

import java.util.List;

public interface BookService {
    List<BookResponse> getAllBooks();
    BookResponse getBookByIsbn(String isbn);
    BookResponse createBook(BookRequest bookRequest);
    void incrementViewCount(String isbn); // 추가: 조회수 증가
}