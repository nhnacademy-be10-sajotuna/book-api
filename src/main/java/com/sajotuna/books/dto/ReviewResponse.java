package com.sajotuna.books.dto;

import com.sajotuna.books.model.Review;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long userId,
        String username,
        String bookIsbn,
        String bookTitle,
        int rating,
        String comment,
        LocalDateTime reviewDate
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getUser().getId(),
                review.getUser().getUsername(),
                review.getBook().getIsbn(),
                review.getBook().getTitle(),
                review.getRating(),
                review.getComment(),
                review.getReviewDate()
        );
    }
}