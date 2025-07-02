package com.sajotuna.books.book.controller;

import com.sajotuna.books.book.controller.request.BookCreateRequest;
import com.sajotuna.books.book.controller.response.BookResponse;
import com.sajotuna.books.book.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books") // RequestMapping 변경
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // --- 일반 사용자/조회용 API ---
    // 모든 책 목록 조회 (페이지네이션 적용)
    @GetMapping
    public ResponseEntity<Page<BookResponse>> getAllBooks(Pageable pageable) {
        Page<BookResponse> books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(books);
    }

    // 특정 책 상세 정보 조회
    @GetMapping("/{isbn}")
    public ResponseEntity<BookResponse> getBookByIsbn(@PathVariable String isbn) {
        BookResponse book = bookService.getBookByIsbn(isbn);
        return ResponseEntity.ok(book);
    }
}