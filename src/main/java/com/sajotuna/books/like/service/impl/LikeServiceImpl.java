package com.sajotuna.books.like.service.impl;

import com.sajotuna.books.book.controller.response.BookResponse;
import com.sajotuna.books.like.controller.request.LikeRequest;
import com.sajotuna.books.like.controller.response.LikeResponse;
import com.sajotuna.books.book.exception.BookNotFoundException; // 변경
import com.sajotuna.books.like.exception.DuplicateLikeException; // 변경
import com.sajotuna.books.like.exception.LikeNotFoundException; // 변경
import com.sajotuna.books.book.domain.Book;
import com.sajotuna.books.like.domain.Like;
import com.sajotuna.books.book.repository.BookRepository;
import com.sajotuna.books.like.repository.LikeRepository;
import com.sajotuna.books.like.service.LikeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final BookRepository bookRepository;

    @Override
    public LikeResponse addLike(Long userId, LikeRequest likeRequest) {
        Book book = bookRepository.findById(likeRequest.getBookIsbn())
                .orElseThrow(() -> new BookNotFoundException(likeRequest.getBookIsbn())); // 예외 변경

        if (likeRepository.findByUserIdAndBookIsbn(userId, book.getIsbn()).isPresent()) {
            throw new DuplicateLikeException(userId, book.getIsbn()); // 예외 변경
        }

        Like like = new Like(userId, book);
        Like savedLike = likeRepository.save(like);

        book.setLikes(book.getLikes() != null ? book.getLikes() + 1 : 1);
        bookRepository.save(book);

        return new LikeResponse(savedLike);
    }

    @Override
    public void removeLike(Long userId, String bookIsbn) {
        Like existingLike = likeRepository.findByUserIdAndBookIsbn(userId, bookIsbn)
                .orElseThrow(() -> new LikeNotFoundException(userId, bookIsbn)); // 예외 변경

        likeRepository.delete(existingLike);

        Book book = bookRepository.findById(bookIsbn)
                .orElseThrow(() -> new BookNotFoundException(bookIsbn)); // 예외 발생 시키지 않도록 하려면 필요
        book.setLikes(book.getLikes() != null && book.getLikes() > 0 ? book.getLikes() - 1 : 0);
        bookRepository.save(book);
    }

    @Override
    public List<BookResponse> getLikedBooksByUserId(Long userId) {
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