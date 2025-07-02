// src/main/java/com/sajotuna/books/book/controller/BookController.java
package com.sajotuna.books.book.controller;

import com.sajotuna.books.book.controller.request.BookCreateRequest;
import com.sajotuna.books.book.controller.response.BookResponse;
import com.sajotuna.books.book.service.BookService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page; // 추가
import org.springframework.data.domain.Pageable; // 추가
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // 모든 책 목록 조회 (페이지네이션 적용)
    @GetMapping
    public ResponseEntity<Page<BookResponse>> getAllBooks(Pageable pageable) { // List -> Page, Pageable 추가
        Page<BookResponse> books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(books);
    }

    // 특정 책 상세 정보 조회
    @GetMapping("/{isbn}")
    public ResponseEntity<BookResponse> getBookByIsbn(@PathVariable String isbn) {
        BookResponse book = bookService.getBookByIsbn(isbn);
        return ResponseEntity.ok(book);
    }

    // 새로운 책 직접 등록
    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookCreateRequest request) {
        BookResponse newBook = bookService.createBook(request);
        return new ResponseEntity<>(newBook, HttpStatus.CREATED);
    }
}