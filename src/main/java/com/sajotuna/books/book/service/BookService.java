package com.sajotuna.books.book.service;

import com.sajotuna.books.book.controller.response.BookResponse;

import java.util.List;

public interface BookService {
    List<BookResponse> getAllBooks();

    BookResponse getBookByIsbn(String isbn); // 추가

    void updateReviewInfo(String isbn, double rating);
}