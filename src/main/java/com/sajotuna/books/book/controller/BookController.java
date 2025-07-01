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
@RequestMapping("/api") // RequestMapping 변경
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // --- 일반 사용자/조회용 API ---
    // 모든 책 목록 조회 (페이지네이션 적용)
    @GetMapping("/books")
    public ResponseEntity<Page<BookResponse>> getAllBooks(Pageable pageable) {
        Page<BookResponse> books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(books);
    }

    // 특정 책 상세 정보 조회
    @GetMapping("/books/{isbn}")
    public ResponseEntity<BookResponse> getBookByIsbn(@PathVariable String isbn) {
        BookResponse book = bookService.getBookByIsbn(isbn);
        return ResponseEntity.ok(book);
    }

    // --- 관리자 전용 API ---
    // 새로운 책 직접 등록 (관리자)
    @PostMapping("/admin/books")
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookCreateRequest request) {
        BookResponse newBook = bookService.createBook(request);
        return new ResponseEntity<>(newBook, HttpStatus.CREATED);
    }

    // 기존 책 정보 수정 (관리자)
    @PutMapping("/admin/books/{isbn}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable String isbn, @Valid @RequestBody BookCreateRequest request) {
        BookResponse updatedBook = bookService.updateBook(isbn, request);
        return ResponseEntity.ok(updatedBook);
    }

    // 도서 삭제 (관리자)
    @DeleteMapping("/admin/books/{isbn}")
    public ResponseEntity<Void> deleteBook(@PathVariable String isbn) {
        bookService.deleteBook(isbn);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // 도서 재고 수정 (관리자)
    @PatchMapping("/admin/books/{isbn}/stock")
    public ResponseEntity<BookResponse> updateBookStock(@PathVariable String isbn, @RequestParam @PositiveOrZero Integer stock) {
        BookResponse updatedBook = bookService.updateBookStock(isbn, stock);
        return ResponseEntity.ok(updatedBook);
    }

    // 도서 좋아요 수 수정 (관리자)
    @PatchMapping("/admin/books/{isbn}/likes")
    public ResponseEntity<BookResponse> updateBookLikes(@PathVariable String isbn, @RequestParam @PositiveOrZero Integer likes) {
        BookResponse updatedBook = bookService.updateBookLikes(isbn, likes);
        return ResponseEntity.ok(updatedBook);
    }
}