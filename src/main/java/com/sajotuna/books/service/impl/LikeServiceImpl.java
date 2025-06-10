// src/main/java/com/sajotuna/books/service/impl/LikeServiceImpl.java
package com.sajotuna.books.service.impl;

import com.sajotuna.books.dto.BookResponse;
import com.sajotuna.books.dto.LikeRequest;
import com.sajotuna.books.dto.LikeResponse;
import com.sajotuna.books.model.Book;
import com.sajotuna.books.model.Like;
import com.sajotuna.books.model.User;
import com.sajotuna.books.repository.BookRepository;
import com.sajotuna.books.repository.LikeRepository;
import com.sajotuna.books.repository.UserRepository;
import com.sajotuna.books.service.LikeService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository; // 사용자 리포지토리 주입
    private final BookRepository bookRepository; // 책 리포지토리 주입

    public LikeServiceImpl(LikeRepository likeRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public LikeResponse addLike(LikeRequest likeRequest) {
        User user = userRepository.findById(likeRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + likeRequest.getUserId()));
        Book book = bookRepository.findById(likeRequest.getBookIsbn())
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ISBN: " + likeRequest.getBookIsbn()));

        // 이미 좋아요를 눌렀는지 확인
        if (likeRepository.findByUserIdAndBookIsbn(user.getId(), book.getIsbn()).isPresent()) {
            throw new IllegalStateException("User already liked this book.");
        }

        Like like = new Like(user, book);
        Like savedLike = likeRepository.save(like);

        // 책의 likes 수 증가 (만약 Book 엔티티에 likes 필드를 유지한다면)
        book.setLikes(book.getLikes() + 1);
        bookRepository.save(book); // 좋아요 수 업데이트

        return new LikeResponse(savedLike);
    }

    @Override
    public void removeLike(Long userId, String bookIsbn) {
        Optional<Like> existingLike = likeRepository.findByUserIdAndBookIsbn(userId, bookIsbn);
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());

            // 책의 likes 수 감소 (만약 Book 엔티티에 likes 필드를 유지한다면)
            Book book = bookRepository.findById(bookIsbn)
                    .orElseThrow(() -> new IllegalArgumentException("Book not found with ISBN: " + bookIsbn));
            book.setLikes(book.getLikes() - 1);
            bookRepository.save(book); // 좋아요 수 업데이트

        } else {
            throw new IllegalArgumentException("Like not found for user " + userId + " and book " + bookIsbn);
        }
    }

    @Override
    public List<BookResponse> getLikedBooksByUserId(Long userId) {
        // 특정 사용자가 '좋아요'를 누른 Like 엔티티 목록을 가져와서
        // 각 Like 엔티티에서 Book 엔티티를 추출한 후 BookResponse로 변환합니다.
        return likeRepository.findByUserId(userId).stream()
                .map(Like::getBook) // Like 객체에서 Book 객체만 추출
                .map(BookResponse::new) // Book 객체를 BookResponse로 변환
                .collect(Collectors.toList());
    }

    @Override
    public boolean isLiked(Long userId, String bookIsbn) {
        return likeRepository.findByUserIdAndBookIsbn(userId, bookIsbn).isPresent();
    }
}