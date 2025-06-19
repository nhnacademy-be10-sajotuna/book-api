package com.sajotuna.books.book.service.impl;

import com.sajotuna.books.book.domain.Book;
import com.sajotuna.books.book.repository.BookRepository;
import com.sajotuna.books.book.service.BookListService;
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
