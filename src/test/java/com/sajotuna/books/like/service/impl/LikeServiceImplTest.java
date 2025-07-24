package com.sajotuna.books.like.service.impl;

import com.sajotuna.books.book.controller.response.BookResponse;
import com.sajotuna.books.book.domain.Book;
import com.sajotuna.books.book.exception.BookNotFoundException;
import com.sajotuna.books.book.repository.BookRepository;
import com.sajotuna.books.like.controller.request.LikeRequest;
import com.sajotuna.books.like.controller.response.LikeResponse;
import com.sajotuna.books.like.domain.Like;
import com.sajotuna.books.like.exception.DuplicateLikeException;
import com.sajotuna.books.like.exception.LikeNotFoundException;
import com.sajotuna.books.like.repository.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private LikeServiceImpl likeService;

    private Book book;
    private Like like;
    private LikeRequest likeRequest;
    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        
        book = new Book();
        book.setIsbn("9788960777330");
        book.setTitle("클린 코드");
        book.setLikes(5);

        like = new Like(userId, book);

        likeRequest = new LikeRequest();
        likeRequest.setBookIsbn("9788960777330");
    }

    @Test
    @DisplayName("좋아요를 추가한다")
    void addLike_Success() {
        // given
        given(bookRepository.findById(likeRequest.getBookIsbn())).willReturn(Optional.of(book));
        given(likeRepository.findByUserIdAndBookIsbn(userId, book.getIsbn())).willReturn(Optional.empty());
        given(likeRepository.save(any(Like.class))).willReturn(like);
        given(bookRepository.save(any(Book.class))).willReturn(book);

        // when
        LikeResponse result = likeService.addLike(userId, likeRequest);

        // then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getBookIsbn()).isEqualTo("9788960777330");
        assertThat(result.getBookTitle()).isEqualTo("클린 코드");
        
        verify(bookRepository).findById(likeRequest.getBookIsbn());
        verify(likeRepository).findByUserIdAndBookIsbn(userId, book.getIsbn());
        verify(likeRepository).save(any(Like.class));
        verify(bookRepository).save(book);
        
        // 좋아요 수 증가 확인
        assertThat(book.getLikes()).isEqualTo(6);
    }

    @Test
    @DisplayName("존재하지 않는 책에 좋아요 추가 시 예외가 발생한다")
    void addLike_BookNotFound_ThrowsException() {
        // given
        given(bookRepository.findById(likeRequest.getBookIsbn())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> likeService.addLike(userId, likeRequest))
                .isInstanceOf(BookNotFoundException.class);
        
        verify(bookRepository).findById(likeRequest.getBookIsbn());
        verify(likeRepository, never()).save(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("이미 좋아요한 책에 중복 좋아요 시 예외가 발생한다")
    void addLike_DuplicateLike_ThrowsException() {
        // given
        given(bookRepository.findById(likeRequest.getBookIsbn())).willReturn(Optional.of(book));
        given(likeRepository.findByUserIdAndBookIsbn(userId, book.getIsbn())).willReturn(Optional.of(like));

        // when & then
        assertThatThrownBy(() -> likeService.addLike(userId, likeRequest))
                .isInstanceOf(DuplicateLikeException.class);
        
        verify(bookRepository).findById(likeRequest.getBookIsbn());
        verify(likeRepository).findByUserIdAndBookIsbn(userId, book.getIsbn());
        verify(likeRepository, never()).save(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("좋아요 수가 null인 책에 좋아요 추가 시 1로 설정된다")
    void addLike_NullLikes_SetsToOne() {
        // given
        book.setLikes(null);
        given(bookRepository.findById(likeRequest.getBookIsbn())).willReturn(Optional.of(book));
        given(likeRepository.findByUserIdAndBookIsbn(userId, book.getIsbn())).willReturn(Optional.empty());
        given(likeRepository.save(any(Like.class))).willReturn(like);
        given(bookRepository.save(any(Book.class))).willReturn(book);

        // when
        likeService.addLike(userId, likeRequest);

        // then
        assertThat(book.getLikes()).isEqualTo(1);
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("좋아요를 제거한다")
    void removeLike_Success() {
        // given
        String bookIsbn = "9788960777330";
        given(likeRepository.findByUserIdAndBookIsbn(userId, bookIsbn)).willReturn(Optional.of(like));
        given(bookRepository.findById(bookIsbn)).willReturn(Optional.of(book));
        given(bookRepository.save(any(Book.class))).willReturn(book);

        // when
        likeService.removeLike(userId, bookIsbn);

        // then
        verify(likeRepository).findByUserIdAndBookIsbn(userId, bookIsbn);
        verify(likeRepository).delete(like);
        verify(bookRepository).findById(bookIsbn);
        verify(bookRepository).save(book);
        
        // 좋아요 수 감소 확인
        assertThat(book.getLikes()).isEqualTo(4);
    }

    @Test
    @DisplayName("존재하지 않는 좋아요 제거 시 예외가 발생한다")
    void removeLike_LikeNotFound_ThrowsException() {
        // given
        String bookIsbn = "9788960777330";
        given(likeRepository.findByUserIdAndBookIsbn(userId, bookIsbn)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> likeService.removeLike(userId, bookIsbn))
                .isInstanceOf(LikeNotFoundException.class);
        
        verify(likeRepository).findByUserIdAndBookIsbn(userId, bookIsbn);
        verify(likeRepository, never()).delete(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("좋아요 수가 0인 책에서 좋아요 제거 시 0을 유지한다")
    void removeLike_ZeroLikes_RemainsZero() {
        // given
        String bookIsbn = "9788960777330";
        book.setLikes(0);
        given(likeRepository.findByUserIdAndBookIsbn(userId, bookIsbn)).willReturn(Optional.of(like));
        given(bookRepository.findById(bookIsbn)).willReturn(Optional.of(book));
        given(bookRepository.save(any(Book.class))).willReturn(book);

        // when
        likeService.removeLike(userId, bookIsbn);

        // then
        assertThat(book.getLikes()).isEqualTo(0);
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("좋아요 수가 null인 책에서 좋아요 제거 시 0으로 설정된다")
    void removeLike_NullLikes_SetsToZero() {
        // given
        String bookIsbn = "9788960777330";
        book.setLikes(null);
        given(likeRepository.findByUserIdAndBookIsbn(userId, bookIsbn)).willReturn(Optional.of(like));
        given(bookRepository.findById(bookIsbn)).willReturn(Optional.of(book));
        given(bookRepository.save(any(Book.class))).willReturn(book);

        // when
        likeService.removeLike(userId, bookIsbn);

        // then
        assertThat(book.getLikes()).isEqualTo(0);
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("사용자가 좋아요한 책 목록을 조회한다")
    void getLikedBooksByUserId_Success() {
        // given
        Book book2 = new Book();
        book2.setIsbn("9788966262281");
        book2.setTitle("이펙티브 자바");
        
        Like like2 = new Like(userId, book2);
        List<Like> likes = Arrays.asList(like, like2);
        
        given(likeRepository.findByUserId(userId)).willReturn(likes);

        // when
        List<BookResponse> result = likeService.getLikedBooksByUserId(userId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(BookResponse::getTitle)
                .containsExactly("클린 코드", "이펙티브 자바");
        assertThat(result).extracting(BookResponse::getIsbn)
                .containsExactly("9788960777330", "9788966262281");
        
        verify(likeRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("좋아요한 책이 없는 사용자는 빈 목록을 반환한다")
    void getLikedBooksByUserId_EmptyList_Success() {
        // given
        given(likeRepository.findByUserId(userId)).willReturn(Arrays.asList());

        // when
        List<BookResponse> result = likeService.getLikedBooksByUserId(userId);

        // then
        assertThat(result).isEmpty();
        verify(likeRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("특정 책에 좋아요했는지 확인한다 - 좋아요함")
    void isLiked_True() {
        // given
        String bookIsbn = "9788960777330";
        given(likeRepository.findByUserIdAndBookIsbn(userId, bookIsbn)).willReturn(Optional.of(like));

        // when
        boolean result = likeService.isLiked(userId, bookIsbn);

        // then
        assertThat(result).isTrue();
        verify(likeRepository).findByUserIdAndBookIsbn(userId, bookIsbn);
    }

    @Test
    @DisplayName("특정 책에 좋아요했는지 확인한다 - 좋아요하지 않음")
    void isLiked_False() {
        // given
        String bookIsbn = "9788960777330";
        given(likeRepository.findByUserIdAndBookIsbn(userId, bookIsbn)).willReturn(Optional.empty());

        // when
        boolean result = likeService.isLiked(userId, bookIsbn);

        // then
        assertThat(result).isFalse();
        verify(likeRepository).findByUserIdAndBookIsbn(userId, bookIsbn);
    }

    @Test
    @DisplayName("좋아요 제거 시 존재하지 않는 책이면 예외가 발생한다")
    void removeLike_BookNotFoundAfterLikeFound_ThrowsException() {
        // given
        String bookIsbn = "9788960777330";
        given(likeRepository.findByUserIdAndBookIsbn(userId, bookIsbn)).willReturn(Optional.of(like));
        given(bookRepository.findById(bookIsbn)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> likeService.removeLike(userId, bookIsbn))
                .isInstanceOf(BookNotFoundException.class);
        
        verify(likeRepository).findByUserIdAndBookIsbn(userId, bookIsbn);
        verify(likeRepository).delete(like);
        verify(bookRepository).findById(bookIsbn);
        verify(bookRepository, never()).save(any());
    }
}