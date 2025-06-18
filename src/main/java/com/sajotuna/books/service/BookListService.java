package com.sajotuna.books.service;

import com.sajotuna.books.model.Book;

import java.util.List;

public interface BookListService {
    void saveAllBooks(List<Book> books);
}
