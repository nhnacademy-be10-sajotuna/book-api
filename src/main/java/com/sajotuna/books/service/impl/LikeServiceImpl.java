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
import com.sajotuna.books.exception.BookNotFoundException; // 추가
import com.sajotuna.books.exception.UserNotFoundException; // 추가
import com.sajotuna.books.exception.DuplicateLikeException; // 추가
import com.sajotuna.books.exception.LikeNotFoundException; // 추가
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public LikeServiceImpl(LikeRepository likeRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public LikeResponse addLike(LikeRequest likeRequest) {
        User user = userRepository.findById(likeRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException(likeRequest.getUserId())); // 예외 변경
        Book book = bookRepository.findById(likeRequest.getBookIsbn())
                .orElseThrow(() -> new BookNotFoundException(likeRequest.getBookIsbn())); // 예외 변경

        if (likeRepository.findByUserIdAndBookIsbn(user.getId(), book.getIsbn()).isPresent()) {
            throw new DuplicateLikeException(user.getId(), book.getIsbn()); // 예외 변경
        }

        Like like = new Like(user, book);
        Like savedLike = likeRepository.save(like);

        // 책의 likes 수 증가
        book.setLikes(book.getLikes() != null ? book.getLikes() + 1 : 1);
        bookRepository.save(book);

        return new LikeResponse(savedLike);
    }

    @Override
    public void removeLike(Long userId, String bookIsbn) {
        Like existingLike = likeRepository.findByUserIdAndBookIsbn(userId, bookIsbn)
                .orElseThrow(() -> new LikeNotFoundException(userId, bookIsbn)); // 예외 변경

        likeRepository.delete(existingLike);

        // 책의 likes 수 감소
        Book book = bookRepository.findById(bookIsbn)
                .orElseThrow(() -> new BookNotFoundException(bookIsbn)); // 이 부분은 좋아요 삭제 전에 책이 없으면 발생하지 않도록 할 수 있지만, 안전을 위해 유지
        book.setLikes(book.getLikes() != null && book.getLikes() > 0 ? book.getLikes() - 1 : 0);
        bookRepository.save(book);
    }

    @Override
    public List<BookResponse> getLikedBooksByUserId(Long userId) {
        // userId가 유효한지 확인하고 싶다면 아래 주석 해제
        // userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        List<Like> likes = likeRepository.findByUserId(userId);
        return likes.stream()
                .map(Like::getBook)
                .map(BookResponse::new)
                .toList();
    }

    @Override
    public boolean isLiked(Long userId, String bookIsbn) {
        return likeRepository.findByUserIdAndBookIsbn(userId, bookIsbn).isPresent();
    }
}