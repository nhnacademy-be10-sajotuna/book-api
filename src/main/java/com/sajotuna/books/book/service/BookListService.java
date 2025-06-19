package com.sajotuna.books.book.service;

import com.sajotuna.books.book.domain.Book;

import java.util.List;

public interface BookListService {
    void saveAllBooks(List<Book> books);
}
