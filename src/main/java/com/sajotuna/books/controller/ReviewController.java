package com.sajotuna.books.controller;

import com.sajotuna.books.dto.ReviewRequest;
import com.sajotuna.books.dto.ReviewResponse;
import com.sajotuna.books.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 새 리뷰 및 평점 등록
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.createReview(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 특정 책의 리뷰 목록 조회
    @GetMapping("/book/{isbn}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByBookIsbn(@PathVariable String isbn) {
        List<ReviewResponse> reviews = reviewService.getReviewsByBookIsbn(isbn);
        if (reviews.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reviews);
    }
}