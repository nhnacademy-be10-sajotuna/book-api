package com.sajotuna.books.service.impl;

import com.sajotuna.books.dto.ReviewRequest;
import com.sajotuna.books.dto.ReviewResponse;
import com.sajotuna.books.exception.BookNotFoundException;
import com.sajotuna.books.exception.UserNotFoundException;
import com.sajotuna.books.model.Book;
import com.sajotuna.books.model.Review;
import com.sajotuna.books.model.User;
import com.sajotuna.books.repository.BookRepository;
import com.sajotuna.books.repository.ReviewRepository;
import com.sajotuna.books.repository.UserRepository;
import com.sajotuna.books.service.ReviewService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Override
    public ReviewResponse createReview(ReviewRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));
        Book book = bookRepository.findById(request.bookIsbn())
                .orElseThrow(() -> new BookNotFoundException(request.bookIsbn()));

        Review review = new Review(user, book, request.rating(), request.comment());
        Review savedReview = reviewRepository.save(review);

        // 책의 평균 평점 및 리뷰 수 업데이트
        updateBookRatingAndReviewCount(book.getIsbn());

        return ReviewResponse.from(savedReview);
    }

    @Override
    public List<ReviewResponse> getReviewsByBookIsbn(String isbn) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
        return reviewRepository.findByBook(book).stream()
                .map(ReviewResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public void updateBookRatingAndReviewCount(String bookIsbn) {
        Book book = bookRepository.findById(bookIsbn)
                .orElseThrow(() -> new BookNotFoundException(bookIsbn));

        List<Review> reviews = reviewRepository.findByBook(book);
        long reviewCount = reviews.size();
        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        book.setReviewCount(reviewCount);
        book.setAverageRating(averageRating);
        bookRepository.save(book);
    }
}