package com.sajotuna.books.book.service.impl;


import com.sajotuna.books.book.controller.response.BookResponse;
import com.sajotuna.books.book.exception.BookNotFoundException; // 변경
import com.sajotuna.books.book.repository.BookRepository;
import com.sajotuna.books.tag.repository.BookTagRepository;
import com.sajotuna.books.category.repository.CategoryRepository;
import com.sajotuna.books.book.service.BookService;
import com.sajotuna.books.tag.service.TagService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final TagService tagService;
    private final BookTagRepository bookTagRepository;




    @Override
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(BookResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public BookResponse getBookByIsbn(String isbn) {
        return bookRepository.findById(isbn)
                .map(BookResponse::new)
                .orElseThrow(() -> new BookNotFoundException(isbn)); // 예외 변경
    }


}