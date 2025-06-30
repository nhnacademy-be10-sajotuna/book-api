package com.sajotuna.books.book.service.impl;


import com.sajotuna.books.book.controller.response.BookResponse;
import com.sajotuna.books.book.domain.Book;
import com.sajotuna.books.book.exception.BookNotFoundException; // 변경
import com.sajotuna.books.book.repository.BookRepository;
import com.sajotuna.books.search.BookSearchDocument;
import com.sajotuna.books.search.repository.BookSearchRepository;
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
    private final BookSearchRepository bookSearchRepository;

    @Override
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(BookResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public BookResponse getBookByIsbn(String isbn) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        book.incrementViewCount();
        book.calculatePopularity();

        bookRepository.save(book); //db 반영
        bookSearchRepository.save(BookSearchDocument.from(book)); // Es 반영
        return new BookResponse(book);
    }

    @Override
    public void updateReviewInfo(String isbn, double rating) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        book.calculateRating(rating);
        book.incrementReviewCount();

        bookRepository.save(book); //  DB 반영

        // Elasticsearch에도 반영
        bookSearchRepository.save(BookSearchDocument.from(book));
    }

}