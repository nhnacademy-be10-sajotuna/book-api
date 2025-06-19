package com.sajotuna.books.like.controller;

import com.sajotuna.books.book.controller.response.BookResponse;
import com.sajotuna.books.like.controller.request.LikeRequest;
import com.sajotuna.books.like.controller.response.LikeResponse;
import com.sajotuna.books.like.service.LikeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    /**
     * 좋아요 추가
     */
    @PostMapping
    public ResponseEntity<LikeResponse> addLike(@RequestHeader("X-User-Id") Long userId ,@Valid @RequestBody LikeRequest likeRequest) {
        LikeResponse response = likeService.addLike(userId, likeRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * 좋아요 취소
     */
    @DeleteMapping
    public ResponseEntity<Void> removeLike(@RequestHeader("X-User-Id") Long userId, @RequestParam String bookIsbn) {
        likeService.removeLike(userId, bookIsbn);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    /**
     * 특정 사용자가 좋아요한 책 목록 조회
     */
    @GetMapping("/user")
    public ResponseEntity<List<BookResponse>> getLikedBooksByUserId(@RequestHeader("X-User-Id") Long userId) {
        List<BookResponse> likedBooks = likeService.getLikedBooksByUserId(userId);
        if (likedBooks.isEmpty()) {
            return ResponseEntity.noContent().build(); // 좋아요한 책이 없을 경우 204 No Content
        }
        return ResponseEntity.ok(likedBooks); // 200 OK
    }

    /**
     * 특정 사용자가 특정 책에 좋아요를 눌렀는지 확인
     */
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkLikeStatus(@RequestHeader("X-User-Id") Long userId, @RequestParam String bookIsbn) {
        boolean isLiked = likeService.isLiked(userId, bookIsbn);
        return ResponseEntity.ok(isLiked); // 200 OK
    }
}