package com.nhnacademy.books.service;

import com.nhnacademy.books.dto.BookRequest;
import com.nhnacademy.books.dto.BookResponse;

import java.util.List;

public interface BookService {
    List<BookResponse> getAllBooks();
    BookResponse getBookByIsbn(String isbn); // 추가
    BookResponse createBook(BookRequest bookRequest);
}