package com.sajotuna.books.book.controller;

import com.sajotuna.books.book.controller.request.BookCreateRequest;
import com.sajotuna.books.book.controller.response.BookResponse;
import com.sajotuna.books.book.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/books")
@RequiredArgsConstructor
public class AdminBookController {

    private final BookService bookService;

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
    public ResponseEntity<Void> updateBookStock(@PathVariable String isbn, @RequestParam @PositiveOrZero Integer stock) {
        bookService.updateBookStock(isbn, stock);
        return ResponseEntity.noContent().build();
    }

    // 도서 좋아요 수 수정 (관리자)
    @PatchMapping("/admin/books/{isbn}/likes")
    public ResponseEntity<BookResponse> updateBookLikes(@PathVariable String isbn, @RequestParam @PositiveOrZero Integer likes) {
        BookResponse updatedBook = bookService.updateBookLikes(isbn, likes);
        return ResponseEntity.ok(updatedBook);
    }
}
