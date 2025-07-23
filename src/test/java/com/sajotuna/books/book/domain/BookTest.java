package com.sajotuna.books.book.domain;

import com.sajotuna.books.book.controller.request.BookCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class BookTest {

    @Test
    @DisplayName("할인율 계산이 정확함")
    void getDiscountRate_ShouldCalculateCorrectly() {
        // Given
        Book book = new Book(
                "9788960777330",
                "테스트 도서",
                "테스트 저자",
                "테스트 출판사", 
                LocalDate.now(),
                300,
                "http://test.com/cover.jpg",
                "테스트 설명",
                15000.0,  // 정가
                13500.0,  // 판매가
                true,
                0
        );

        // When
        Double discountRate = book.getDiscountRate();

        // Then
        assertThat(discountRate).isEqualTo(10.0); // 10% 할인
    }

    @Test
    @DisplayName("정가가 0이거나 null인 경우 할인율은 0%")
    void getDiscountRate_ShouldReturn0WhenOriginalPriceIsZeroOrNull() {
        // Given
        Book book = new Book(
                "9788960777330",
                "테스트 도서",
                "테스트 저자",
                "테스트 출판사",
                LocalDate.now(),
                300,
                "http://test.com/cover.jpg", 
                "테스트 설명",
                0.0,     // 정가 0
                13500.0, // 판매가
                true,
                0
        );

        // When
        Double discountRate = book.getDiscountRate();

        // Then
        assertThat(discountRate).isEqualTo(0.0);

        // null인 경우
        book.setOriginalPrice(null);
        assertThat(book.getDiscountRate()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("좋아요 수 업데이트가 정상적으로 동작함")
    void updateLikes_ShouldUpdateLikesCorrectly() {
        // Given
        Book book = new Book(
                "9788960777330",
                "테스트 도서",
                "테스트 저자",
                "테스트 출판사",
                LocalDate.now(),
                300,
                "http://test.com/cover.jpg",
                "테스트 설명", 
                15000.0,
                13500.0,
                true,
                0
        );

        // When
        book.updateLikes(50);

        // Then
        assertThat(book.getLikes()).isEqualTo(50);
    }

    @Test
    @DisplayName("도서 정보 업데이트가 정상적으로 동작함")
    void updateInfo_ShouldUpdateBookInfoCorrectly() {
        // Given
        Book book = new Book(
                "9788960777330",
                "기존 제목",
                "기존 저자",
                "기존 출판사",
                LocalDate.of(2020, 1, 1),
                200,
                "http://old.com/cover.jpg",
                "기존 설명",
                10000.0,
                9000.0,
                false,
                10
        );

        BookCreateRequest request = new BookCreateRequest();
        request.setTitle("새로운 제목");
        request.setAuthor("새로운 저자");
        request.setPublisher("새로운 출판사");
        request.setPublicationDate(LocalDate.of(2023, 5, 15));
        request.setPageCount(350);
        request.setImageUrl("http://new.com/cover.jpg");
        request.setDescription("새로운 설명");
        request.setOriginalPrice(20000.0);
        request.setSellingPrice(18000.0);
        request.setGiftWrappingAvailable(true);

        // When
        book.updateInfo(request);

        // Then - 좋아요 수는 변경되지 않음
        assertThat(book.getTitle()).isEqualTo("새로운 제목");
        assertThat(book.getAuthor()).isEqualTo("새로운 저자");
        assertThat(book.getPublisher()).isEqualTo("새로운 출판사");
        assertThat(book.getPublicationDate()).isEqualTo(LocalDate.of(2023, 5, 15));
        assertThat(book.getPageCount()).isEqualTo(350);
        assertThat(book.getImageUrl()).isEqualTo("http://new.com/cover.jpg");
        assertThat(book.getDescription()).isEqualTo("새로운 설명");
        assertThat(book.getOriginalPrice()).isEqualTo(20000.0);
        assertThat(book.getSellingPrice()).isEqualTo(18000.0);
        assertThat(book.getGiftWrappingAvailable()).isTrue();
        assertThat(book.getLikes()).isEqualTo(10); // 좋아요 수는 변경되지 않음
    }
}