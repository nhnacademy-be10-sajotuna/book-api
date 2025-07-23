package com.sajotuna.books.book.service;

import com.sajotuna.books.book.OrderStockClient;
import com.sajotuna.books.book.controller.request.StockRequest;
import com.sajotuna.books.book.domain.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AladinStockServiceTest {

    @Mock
    private OrderStockClient orderStockClient;

    @InjectMocks
    private AladinStockService aladinStockService;

    @Test
    @DisplayName("재고 동기화 성공 - 도서 목록이 있을 때")
    void syncStockWithOrderApi_ShouldSyncStock_WhenBooksExist() {
        // Given
        Book book1 = createTestBook("9788960777330", "테스트 도서1");
        Book book2 = createTestBook("9788960777331", "테스트 도서2");
        List<Book> books = List.of(book1, book2);

        // When
        aladinStockService.syncStockWithOrderApi(books);

        // Then
        verify(orderStockClient, times(1)).createStocks(anyList());
        verify(orderStockClient).createStocks(argThat(stockRequests -> 
            stockRequests.size() == 2 &&
            stockRequests.get(0).isbn().equals("9788960777330") &&
            stockRequests.get(0).stock() == 100 &&
            stockRequests.get(1).isbn().equals("9788960777331") &&
            stockRequests.get(1).stock() == 100
        ));
    }

    @Test
    @DisplayName("재고 동기화 - 빈 도서 목록일 때 API 호출하지 않음")
    void syncStockWithOrderApi_ShouldNotCallApi_WhenBooksEmpty() {
        // Given
        List<Book> emptyBooks = List.of();

        // When
        aladinStockService.syncStockWithOrderApi(emptyBooks);

        // Then
        verify(orderStockClient, never()).createStocks(anyList());
    }

    @Test
    @DisplayName("재고 동기화 - 단일 도서")
    void syncStockWithOrderApi_ShouldSyncStock_WhenSingleBook() {
        // Given
        Book book = createTestBook("9788960777330", "테스트 도서");
        List<Book> books = List.of(book);

        // When
        aladinStockService.syncStockWithOrderApi(books);

        // Then
        verify(orderStockClient, times(1)).createStocks(anyList());
        verify(orderStockClient).createStocks(argThat(stockRequests -> 
            stockRequests.size() == 1 &&
            stockRequests.get(0).isbn().equals("9788960777330") &&
            stockRequests.get(0).stock() == 100
        ));
    }

    @Test
    @DisplayName("OrderStockClient 예외 발생 시 예외 전파")
    void syncStockWithOrderApi_ShouldPropagateException_WhenClientThrowsException() {
        // Given
        Book book = createTestBook("9788960777330", "테스트 도서");
        List<Book> books = List.of(book);
        doThrow(new RuntimeException("Stock API 오류")).when(orderStockClient).createStocks(anyList());

        // When & Then
        assertThatThrownBy(() -> aladinStockService.syncStockWithOrderApi(books))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Stock API 오류");
        verify(orderStockClient, times(1)).createStocks(anyList());
    }

    @Test
    @DisplayName("기본 재고 수량이 100으로 설정됨")
    void syncStockWithOrderApi_ShouldSetDefaultStock100() {
        // Given
        Book book = createTestBook("9788960777330", "테스트 도서");
        List<Book> books = List.of(book);

        // When
        aladinStockService.syncStockWithOrderApi(books);

        // Then
        verify(orderStockClient).createStocks(argThat(stockRequests ->
            stockRequests.stream().allMatch(request -> request.stock() == 100)
        ));
    }

    @Test
    @DisplayName("대용량 도서 목록 처리 - 1000권")
    void syncStockWithOrderApi_ShouldHandleLargeBookList() {
        // Given
        List<Book> largeBookList = IntStream.range(0, 1000)
                .mapToObj(i -> createTestBook("isbn" + i, "book" + i))
                .collect(Collectors.toList());

        // When
        aladinStockService.syncStockWithOrderApi(largeBookList);

        // Then
        verify(orderStockClient, times(1)).createStocks(anyList());
        verify(orderStockClient).createStocks(argThat(stockRequests ->
            stockRequests.size() == 1000 &&
            stockRequests.stream().allMatch(request -> request.stock() == 100)
        ));
    }

    @Test
    @DisplayName("ISBN이 null인 도서도 처리됨")
    void syncStockWithOrderApi_ShouldHandleNullIsbn() {
        // Given
        Book bookWithNullIsbn = new Book(
                null, // ISBN null
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
        List<Book> books = List.of(bookWithNullIsbn);

        // When
        aladinStockService.syncStockWithOrderApi(books);

        // Then
        verify(orderStockClient).createStocks(argThat(stockRequests ->
            stockRequests.size() == 1 &&
            stockRequests.get(0).isbn() == null &&
            stockRequests.get(0).stock() == 100
        ));
    }

    @Test
    @DisplayName("ISBN이 빈 문자열인 도서도 처리됨")
    void syncStockWithOrderApi_ShouldHandleEmptyIsbn() {
        // Given
        Book bookWithEmptyIsbn = createTestBook("", "테스트 도서");
        List<Book> books = List.of(bookWithEmptyIsbn);

        // When
        aladinStockService.syncStockWithOrderApi(books);

        // Then
        verify(orderStockClient).createStocks(argThat(stockRequests ->
            stockRequests.size() == 1 &&
            stockRequests.get(0).isbn().equals("") &&
            stockRequests.get(0).stock() == 100
        ));
    }

    private Book createTestBook(String isbn, String title) {
        return new Book(
                isbn,
                title,
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
    }
}