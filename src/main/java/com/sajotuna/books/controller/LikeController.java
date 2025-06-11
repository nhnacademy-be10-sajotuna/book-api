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
@RequestMapping("/api/likes") // 좋아요 관련 API 엔드포인트는 /api/likes로 시작
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
        try {
            LikeResponse response = likeService.addLike(likeRequest);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // 사용자 또는 책을 찾을 수 없는 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            // 이미 좋아요를 누른 경우
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    /**
     * 좋아요 취소
     * DELETE /api/likes?userId=1&bookIsbn=978-0321765723
     */
    @DeleteMapping
    public ResponseEntity<Void> removeLike(@RequestParam Long userId, @RequestParam String bookIsbn) {
        try {
            likeService.removeLike(userId, bookIsbn);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (IllegalArgumentException e) {
            // 좋아요를 찾을 수 없거나 사용자/책이 유효하지 않은 경우
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
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