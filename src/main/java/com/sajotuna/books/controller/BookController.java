// src/main/java/com/sajotuna/books/controller/BookController.java
package com.sajotuna.books.controller;

import com.sajotuna.books.dto.*;
import com.sajotuna.books.service.BookService;
import com.sajotuna.books.service.CategoryService;
import com.sajotuna.books.service.LikeService; // LikeService 추가
import com.sajotuna.books.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final LikeService likeService; // LikeService 주입

    public BookController(BookService bookService, CategoryService categoryService, TagService tagService, LikeService likeService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
        this.tagService = tagService;
        this.likeService = likeService; // 주입
    }

    // 모든 책 목록 조회
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<BookResponse> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    // 특정 책 상세 정보 조회
    @GetMapping("/{isbn}")
    public ResponseEntity<BookResponse> getBookByIsbn(@PathVariable String isbn) {
        BookResponse book = bookService.getBookByIsbn(isbn);
        if (book != null) {
            return ResponseEntity.ok(book);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 책 등록
    @PostMapping
    public ResponseEntity<BookResponse> createBook(@RequestBody BookRequest bookRequest) {
        BookResponse createdBook = bookService.createBook(bookRequest);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    // 카테고리 등록
    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest categoryRequest) {
        CategoryResponse createdCategory = categoryService.createCategory(categoryRequest);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    // 모든 카테고리 조회
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // 태그 등록
    @PostMapping("/tags")
    public ResponseEntity<TagResponse> createTag(@RequestBody TagRequest tagRequest) {
        TagResponse createdTag = tagService.createTag(tagRequest);
        return new ResponseEntity<>(createdTag, HttpStatus.CREATED);
    }

    // 모든 태그 목록 조회
    @GetMapping("/tags")
    public ResponseEntity<List<TagResponse>> getAllTags() {
        List<TagResponse> tags = tagService.getAllTags();
        return ResponseEntity.ok(tags);
    }

    // 특정 태그 상세 정보 조회
    @GetMapping("/tags/{id}")
    public ResponseEntity<TagResponse> getTagById(@PathVariable Long id) {
        TagResponse tag = tagService.getTagById(id);
        if (tag != null) {
            return ResponseEntity.ok(tag);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 태그 삭제
    @DeleteMapping("/tags/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }


    // '좋아요' 추가 (사용자 ID와 책 ISBN 필요)
    @PostMapping("/likes")
    public ResponseEntity<LikeResponse> addLike(@RequestBody LikeRequest likeRequest) {
        LikeResponse response = likeService.addLike(likeRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // '좋아요' 취소 (사용자 ID와 책 ISBN 필요)
    @DeleteMapping("/likes")
    public ResponseEntity<Void> removeLike(@RequestParam Long userId, @RequestParam String bookIsbn) {
        likeService.removeLike(userId, bookIsbn);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // 특정 사용자가 '좋아요'한 책 목록 조회 (마이페이지용)
    // URL 예시: /api/books/users/1/likes
    @GetMapping("/users/{userId}/likes")
    public ResponseEntity<List<BookResponse>> getLikedBooksByUser(@PathVariable Long userId) {
        List<BookResponse> likedBooks = likeService.getLikedBooksByUserId(userId);
        return ResponseEntity.ok(likedBooks);
    }

    // 특정 사용자가 특정 책에 '좋아요'를 눌렀는지 확인
    // URL 예시: /api/books/is-liked?userId=1&bookIsbn=9788966262174
    @GetMapping("/is-liked")
    public ResponseEntity<Boolean> isLiked(@RequestParam Long userId, @RequestParam String bookIsbn) {
        boolean liked = likeService.isLiked(userId, bookIsbn);
        return ResponseEntity.ok(liked);
    }
}