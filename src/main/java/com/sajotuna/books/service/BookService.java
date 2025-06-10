package com.sajotuna.books.service;

import com.sajotuna.books.dto.BookRequest;
import com.sajotuna.books.dto.BookResponse;

import java.util.List;

public interface BookService {
    List<BookResponse> getAllBooks();
    BookResponse getBookByIsbn(String isbn); // 추가
    BookResponse createBook(BookRequest bookRequest);
}