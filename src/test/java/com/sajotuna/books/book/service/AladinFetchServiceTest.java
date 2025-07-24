package com.sajotuna.books.book.service;

import com.sajotuna.books.book.controller.response.AladinBookResponse;
import com.sajotuna.books.book.controller.response.ItemSearchResponse;
import com.sajotuna.books.book.exception.ExternalApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AladinFetchServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AladinFetchService aladinFetchService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(aladinFetchService, "BASE_URL", "http://localhost:8080");
        ReflectionTestUtils.setField(aladinFetchService, "TTB_KEY", "key");
    }

    @Test
    @DisplayName("도서 검색 성공 - 단일 페이지")
    void fetchBooks_ShouldReturnBooks_WhenSinglePage() {
        // Given
        String keyword = "자바";
        int totalPages = 1;
        
        AladinBookResponse book1 = createAladinBookResponse("9788960777330", "자바 프로그래밍");
        AladinBookResponse book2 = createAladinBookResponse("9788960777331", "자바 완전정복");
        
        ItemSearchResponse mockResponse = new ItemSearchResponse();
        mockResponse.setItem(List.of(book1, book2));
        
        when(restTemplate.getForObject(anyString(), eq(ItemSearchResponse.class)))
                .thenReturn(mockResponse);

        // When
        List<AladinBookResponse> result = aladinFetchService.fetchBooks(keyword, totalPages);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getIsbn()).isEqualTo("9788960777330");
        assertThat(result.get(1).getIsbn()).isEqualTo("9788960777331");
        verify(restTemplate, times(1)).getForObject(anyString(), eq(ItemSearchResponse.class));
    }

    @Test
    @DisplayName("도서 검색 성공 - 다중 페이지")
    void fetchBooks_ShouldReturnBooks_WhenMultiplePages() {
        // Given
        String keyword = "자바";
        int totalPages = 3;
        
        // 페이지별 응답 생성
        AladinBookResponse book1 = createAladinBookResponse("9788960777330", "자바 프로그래밍1");
        AladinBookResponse book2 = createAladinBookResponse("9788960777331", "자바 프로그래밍2");
        AladinBookResponse book3 = createAladinBookResponse("9788960777332", "자바 프로그래밍3");
        
        ItemSearchResponse response1 = new ItemSearchResponse();
        response1.setItem(List.of(book1));
        
        ItemSearchResponse response2 = new ItemSearchResponse();
        response2.setItem(List.of(book2));
        
        ItemSearchResponse response3 = new ItemSearchResponse();
        response3.setItem(List.of(book3));
        
        when(restTemplate.getForObject(anyString(), eq(ItemSearchResponse.class)))
                .thenReturn(response1, response2, response3);

        // When
        List<AladinBookResponse> result = aladinFetchService.fetchBooks(keyword, totalPages);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getIsbn()).isEqualTo("9788960777330");
        assertThat(result.get(1).getIsbn()).isEqualTo("9788960777331");
        assertThat(result.get(2).getIsbn()).isEqualTo("9788960777332");
        verify(restTemplate, times(3)).getForObject(anyString(), eq(ItemSearchResponse.class));
    }

    @Test
    @DisplayName("도서 검색 - 빈 응답")
    void fetchBooks_ShouldReturnEmptyList_WhenNoResults() {
        // Given
        String keyword = "존재하지않는키워드";
        int totalPages = 1;
        
        ItemSearchResponse mockResponse = new ItemSearchResponse();
        mockResponse.setItem(List.of());
        
        when(restTemplate.getForObject(anyString(), eq(ItemSearchResponse.class)))
                .thenReturn(mockResponse);

        // When
        List<AladinBookResponse> result = aladinFetchService.fetchBooks(keyword, totalPages);

        // Then
        assertThat(result).isEmpty();
        verify(restTemplate, times(1)).getForObject(anyString(), eq(ItemSearchResponse.class));
    }

    @Test
    @DisplayName("도서 검색 - null 응답")
    void fetchBooks_ShouldReturnEmptyList_WhenNullResponse() {
        // Given
        String keyword = "자바";
        int totalPages = 1;
        
        when(restTemplate.getForObject(anyString(), eq(ItemSearchResponse.class)))
                .thenReturn(null);

        // When
        List<AladinBookResponse> result = aladinFetchService.fetchBooks(keyword, totalPages);

        // Then
        assertThat(result).isEmpty();
        verify(restTemplate, times(1)).getForObject(anyString(), eq(ItemSearchResponse.class));
    }

    @Test
    @DisplayName("도서 검색 - 외부 API 예외 발생")
    void fetchBooks_ShouldThrowException_WhenApiError() {
        // Given
        String keyword = "자바";
        int totalPages = 1;
        
        when(restTemplate.getForObject(anyString(), eq(ItemSearchResponse.class)))
                .thenThrow(new RuntimeException("API 호출 실패"));

        // When & Then
        assertThatThrownBy(() -> aladinFetchService.fetchBooks(keyword, totalPages))
                .isInstanceOf(ExternalApiException.class);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(ItemSearchResponse.class));
    }

    @Test
    @DisplayName("도서 검색 - 0 페이지 요청")
    void fetchBooks_ShouldReturnEmptyList_WhenZeroPages() {
        // Given
        String keyword = "자바";
        int totalPages = 0;

        // When
        List<AladinBookResponse> result = aladinFetchService.fetchBooks(keyword, totalPages);

        // Then
        assertThat(result).isEmpty();
        verify(restTemplate, never()).getForObject(anyString(), eq(ItemSearchResponse.class));
    }

    private AladinBookResponse createAladinBookResponse(String isbn, String title) {
        AladinBookResponse response = new AladinBookResponse();
        response.setIsbn(isbn);
        response.setTitle(title);
        response.setAuthor("테스트 저자");
        response.setPublisher("테스트 출판사");
        response.setPubDate("2023-01-01");
        response.setDescription("테스트 설명");
        response.setCover("http://test.com/cover.jpg");
        response.setCategoryName("IT>프로그래밍");
        response.setPriceStandard(15000);
        response.setPriceSales(13500);
        return response;
    }
}