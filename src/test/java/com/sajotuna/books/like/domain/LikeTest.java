package com.sajotuna.books.like.domain;

import com.sajotuna.books.book.domain.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LikeTest {

    private Book book;
    private Long userId;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setIsbn("9788960777330");
        book.setTitle("클린 코드");
        
        userId = 1L;
    }

    @Test
    @DisplayName("userId와 Book으로 Like 객체를 생성한다")
    void createLike_WithUserIdAndBook_Success() {
        // when
        Like like = new Like(userId, book);

        // then
        assertThat(like.getUserId()).isEqualTo(userId);
        assertThat(like.getBook()).isEqualTo(book);
        assertThat(like.getId()).isNull(); // 아직 영속화되지 않음
    }

    @Test
    @DisplayName("기본 생성자로 Like 객체를 생성한다")
    void createLike_WithNoArgsConstructor_Success() {
        // when
        Like like = new Like();

        // then
        assertThat(like.getUserId()).isNull();
        assertThat(like.getBook()).isNull();
        assertThat(like.getId()).isNull();
    }

    @Test
    @DisplayName("Like의 ID를 설정하고 조회할 수 있다")
    void setAndGetId_Success() {
        // given
        Like like = new Like(userId, book);
        Long id = 1L;

        // when
        like.setId(id);

        // then
        assertThat(like.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("Like의 userId를 설정하고 조회할 수 있다")
    void setAndGetUserId_Success() {
        // given
        Like like = new Like();
        Long newUserId = 2L;

        // when
        like.setUserId(newUserId);

        // then
        assertThat(like.getUserId()).isEqualTo(newUserId);
    }

    @Test
    @DisplayName("Like의 Book을 설정하고 조회할 수 있다")
    void setAndGetBook_Success() {
        // given
        Like like = new Like();
        Book newBook = new Book();
        newBook.setIsbn("9788966262281");
        newBook.setTitle("이펙티브 자바");

        // when
        like.setBook(newBook);

        // then
        assertThat(like.getBook()).isEqualTo(newBook);
        assertThat(like.getBook().getIsbn()).isEqualTo("9788966262281");
        assertThat(like.getBook().getTitle()).isEqualTo("이펙티브 자바");
    }

    @Test
    @DisplayName("null userId로 Like를 생성할 수 있다")
    void createLike_WithNullUserId_Success() {
        // given
        Long nullUserId = null;

        // when
        Like like = new Like(nullUserId, book);

        // then
        assertThat(like.getUserId()).isNull();
        assertThat(like.getBook()).isEqualTo(book);
    }

    @Test
    @DisplayName("null Book으로 Like를 생성할 수 있다")
    void createLike_WithNullBook_Success() {
        // given
        Book nullBook = null;

        // when
        Like like = new Like(userId, nullBook);

        // then
        assertThat(like.getUserId()).isEqualTo(userId);
        assertThat(like.getBook()).isNull();
    }

    @Test
    @DisplayName("0L userId로 Like를 생성할 수 있다")
    void createLike_WithZeroUserId_Success() {
        // given
        Long zeroUserId = 0L;

        // when
        Like like = new Like(zeroUserId, book);

        // then
        assertThat(like.getUserId()).isEqualTo(0L);
        assertThat(like.getBook()).isEqualTo(book);
    }

    @Test
    @DisplayName("음수 userId로 Like를 생성할 수 있다")
    void createLike_WithNegativeUserId_Success() {
        // given
        Long negativeUserId = -1L;

        // when
        Like like = new Like(negativeUserId, book);

        // then
        assertThat(like.getUserId()).isEqualTo(-1L);
        assertThat(like.getBook()).isEqualTo(book);
    }

    @Test
    @DisplayName("매우 큰 userId로 Like를 생성할 수 있다")
    void createLike_WithLargeUserId_Success() {
        // given
        Long largeUserId = Long.MAX_VALUE;

        // when
        Like like = new Like(largeUserId, book);

        // then
        assertThat(like.getUserId()).isEqualTo(Long.MAX_VALUE);
        assertThat(like.getBook()).isEqualTo(book);
    }

    @Test
    @DisplayName("Book 정보가 변경되어도 Like 객체는 참조를 유지한다")
    void like_BookReference_MaintainedAfterBookChange() {
        // given
        Like like = new Like(userId, book);
        String originalTitle = book.getTitle();

        // when
        book.setTitle("변경된 제목");

        // then
        assertThat(like.getBook()).isEqualTo(book);
        assertThat(like.getBook().getTitle()).isEqualTo("변경된 제목");
        assertThat(like.getBook().getTitle()).isNotEqualTo(originalTitle);
    }
}