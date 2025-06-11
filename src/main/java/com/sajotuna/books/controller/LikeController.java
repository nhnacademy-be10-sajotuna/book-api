package com.sajotuna.books.controller;

import com.sajotuna.books.dto.BookResponse;
import com.sajotuna.books.dto.LikeRequest;
import com.sajotuna.books.dto.LikeResponse;
import com.sajotuna.books.service.LikeService;
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
     * POST /api/likes
     * Request Body: { "userId": 1, "bookIsbn": "978-0321765723" }
     */
    @PostMapping
    public ResponseEntity<LikeResponse> addLike(@RequestBody LikeRequest likeRequest) {
        LikeResponse response = likeService.addLike(likeRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * 좋아요 취소
     * DELETE /api/likes?userId=1&bookIsbn=978-0321765723
     */
    @DeleteMapping
    public ResponseEntity<Void> removeLike(@RequestParam Long userId, @RequestParam String bookIsbn) {
        likeService.removeLike(userId, bookIsbn);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    /**
     * 특정 사용자가 좋아요한 책 목록 조회
     * GET /api/likes/user/{userId}
     * 예시: GET /api/likes/user/1
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookResponse>> getLikedBooksByUserId(@PathVariable Long userId) {
        List<BookResponse> likedBooks = likeService.getLikedBooksByUserId(userId);
        if (likedBooks.isEmpty()) {
            return ResponseEntity.noContent().build(); // 좋아요한 책이 없을 경우 204 No Content
        }
        return ResponseEntity.ok(likedBooks); // 200 OK
    }

    /**
     * 특정 사용자가 특정 책에 좋아요를 눌렀는지 확인
     * GET /api/likes/check?userId=1&bookIsbn=978-0321765723
     */
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkLikeStatus(@RequestParam Long userId, @RequestParam String bookIsbn) {
        boolean isLiked = likeService.isLiked(userId, bookIsbn);
        return ResponseEntity.ok(isLiked); // 200 OK
    }
}