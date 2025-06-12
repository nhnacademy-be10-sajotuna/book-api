package com.sajotuna.books.controller;

import com.sajotuna.books.dto.BookResponse;
import com.sajotuna.books.dto.BookRequest;
import com.sajotuna.books.service.BookService;
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

    // 모든 책 목록 조회
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<BookResponse> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    // 특정 책 상세 정보 조회 및 조회수 증가
    @GetMapping("/{isbn}")
    public ResponseEntity<BookResponse> getBookByIsbn(@PathVariable String isbn) {
        BookResponse book = bookService.getBookByIsbn(isbn);
        // 조회수 증가 로직 (SearchService로 옮길 수도 있으나, 여기서는 상세 조회 시 증가)
        bookService.incrementViewCount(isbn);
        return ResponseEntity.ok(book);
    }

    // 책 등록
    @PostMapping
    public ResponseEntity<BookResponse> createBook(@RequestBody BookRequest bookRequest) {
        BookResponse createdBook = bookService.createBook(bookRequest);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }
}