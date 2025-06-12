package com.sajotuna.books.service;

import com.sajotuna.books.dto.ReviewRequest;
import com.sajotuna.books.dto.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequest request);
    List<ReviewResponse> getReviewsByBookIsbn(String isbn);
    void updateBookRatingAndReviewCount(String bookIsbn); // 책의 평균 평점 및 리뷰 수 업데이트
}