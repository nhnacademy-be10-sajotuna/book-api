package com.sajotuna.books.service.impl;

import com.sajotuna.books.model.Book;
import com.sajotuna.books.repository.BookRepository;
import com.sajotuna.books.service.BookListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookListServiceImpl implements BookListService {

    private final BookRepository bookRepository;

    @Override
    public void saveAllBooks(List<Book> books) {
        bookRepository.saveAll(books);
    }
}
