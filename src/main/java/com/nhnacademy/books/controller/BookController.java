package com.nhnacademy.books.controller;

import com.nhnacademy.books.dto.BookResponse; // 추가
import com.nhnacademy.books.dto.CategoryResponse; // 추가
import com.nhnacademy.books.model.Book;
import com.nhnacademy.books.model.Category;
import com.nhnacademy.books.service.BookService;
import com.nhnacademy.books.service.CategoryService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final CategoryService categoryService;

    public BookController(BookService bookService, CategoryService categoryService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
    }

    // 새 도서 등록 (POST /api/books)
    @PostMapping
    public ResponseEntity<BookResponse> registerBook(@RequestBody BookRegisterRequest request) {
        try {
            Book registeredBook = bookService.registerBook(
                    request.getIsbn(),
                    request.getTitle(),
                    request.getAuthor(),
                    request.getPublisher(),
                    request.getPublicationDate(),
                    request.getOriginalPrice(),
                    request.getSellingPrice(),
                    request.isGiftWrappingAvailable(),
                    request.getCategoryIds(),
                    request.getTags()
            );
            return new ResponseEntity<>(new BookResponse(registeredBook), HttpStatus.CREATED); // DTO로 변환하여 반환
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // 모든 도서 조회 (GET /api/books)
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        List<BookResponse> bookResponses = books.stream()
                .map(BookResponse::new) // Book 엔티티를 BookResponse DTO로 변환
                .collect(Collectors.toList());
        return new ResponseEntity<>(bookResponses, HttpStatus.OK);
    }

    // ISBN으로 도서 조회 (GET /api/books/{isbn})
    @GetMapping("/{isbn}")
    public ResponseEntity<BookResponse> getBookByIsbn(@PathVariable String isbn) {
        Optional<Book> book = bookService.getBookByIsbn(isbn);
        return book.map(value -> new ResponseEntity<>(new BookResponse(value), HttpStatus.OK)) // DTO로 변환하여 반환
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 제목으로 도서 검색 (GET /api/books/search/title?keyword=...)
    @GetMapping("/search/title")
    public ResponseEntity<List<BookResponse>> searchBooksByTitle(@RequestParam String keyword) {
        List<Book> books = bookService.searchBooksByTitle(keyword);
        List<BookResponse> bookResponses = books.stream()
                .map(BookResponse::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(bookResponses, HttpStatus.OK);
    }

    // 저자로 도서 검색 (GET /api/books/search/author?keyword=...)
    @GetMapping("/search/author")
    public ResponseEntity<List<BookResponse>> searchBooksByAuthor(@RequestParam String keyword) {
        List<Book> books = bookService.searchBooksByAuthor(keyword);
        List<BookResponse> bookResponses = books.stream()
                .map(BookResponse::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(bookResponses, HttpStatus.OK);
    }

    // 도서 정보 업데이트 (PUT /api/books/{isbn})
    @PutMapping("/{isbn}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable String isbn, @RequestBody BookUpdateRequest request) {
        try {
            Book updatedBook = bookService.updateBookDetails(
                    isbn,
                    request.getTitle(),
                    request.getTableOfContents(),
                    request.getDescription(),
                    request.getAuthor(),
                    request.getPublisher(),
                    request.getPublicationDate(),
                    request.getOriginalPrice(),
                    request.getSellingPrice(),
                    request.isGiftWrappingAvailable(),
                    request.getCategoryIds(),
                    request.getTags()
            );
            return new ResponseEntity<>(new BookResponse(updatedBook), HttpStatus.OK); // DTO로 변환하여 반환
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // 도서 삭제 (DELETE /api/books/{isbn})
    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> deleteBook(@PathVariable String isbn) {
        try {
            bookService.deleteBook(isbn);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 도서 좋아요 (POST /api/books/{isbn}/like)
    @PostMapping("/{isbn}/like")
    public ResponseEntity<Void> likeBook(@PathVariable String isbn) {
        try {
            bookService.likeBook(isbn);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 도서 좋아요 취소 (POST /api/books/{isbn}/unlike)
    @PostMapping("/{isbn}/unlike")
    public ResponseEntity<Void> unlikeBook(@PathVariable String isbn) {
        try {
            bookService.unlikeBook(isbn);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 카테고리 관련 API (간단하게)
    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryCreateRequest request) {
        try {
            Category category = categoryService.createCategory(request.getName(), request.getParentId());
            return new ResponseEntity<>(new CategoryResponse(category), HttpStatus.CREATED); // DTO로 변환하여 반환
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryResponse> categoryResponses = categories.stream()
                .map(CategoryResponse::new) // Category 엔티티를 CategoryResponse DTO로 변환
                .collect(Collectors.toList());
        return new ResponseEntity<>(categoryResponses, HttpStatus.OK);
    }
}

// 요청 DTO (Data Transfer Object) - 클라이언트로부터 받은 데이터를 매핑
// 실제 프로젝트에서는 DTO를 별도 패키지에 관리하는 것이 좋습니다.
@Getter
@Setter
@NoArgsConstructor
class BookRegisterRequest {
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private LocalDate publicationDate;
    private double originalPrice;
    private double sellingPrice;
    private boolean giftWrappingAvailable;
    private List<Long> categoryIds; // 카테고리 ID 목록
    private List<String> tags;
}

@Getter
@Setter
@NoArgsConstructor
class BookUpdateRequest {
    private String title;
    private String tableOfContents;
    private String description;
    private String author;
    private String publisher;
    private LocalDate publicationDate;
    private double originalPrice;
    private double sellingPrice;
    private boolean giftWrappingAvailable;
    private List<Long> categoryIds;
    private List<String> tags;
}

@Getter
@Setter
@NoArgsConstructor
class CategoryCreateRequest {
    private String name;
    private Long parentId;
}