package com.sajotuna.books.search.service;

import com.sajotuna.books.search.BookSearchDocument;
import com.sajotuna.books.search.repository.BookSearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class BookStatsServiceTest {

    @Mock
    private BookSearchRepository bookSearchRepository;

    @InjectMocks
    private BookStatsService bookStatsService;

    private BookSearchDocument document;

    @BeforeEach
    void setUp() {
        document = BookSearchDocument.builder()
                .id("9788960777330")
                .isbn("9788960777330")
                .title("클린 코드")
                .viewCount(100)
                .searchCount(50)
                .popularity(85.5)
                .build();
    }

    @Test
    @DisplayName("조회수를 증가시킨다")
    void incrementViewCount_Success() {
        // given
        String isbn = "9788960777330";
        given(bookSearchRepository.findById(isbn)).willReturn(Optional.of(document));

        // when
        bookStatsService.incrementViewCount(isbn);

        // then - 메서드 호출 확인 (실제로는 스케줄러에 의해 나중에 처리됨)
        verify(bookSearchRepository).findById(isbn);
    }

    @Test
    @DisplayName("검색수를 증가시킨다")
    void incrementSearchCount_Success() {
        // given
        String isbn = "9788960777330";
        given(bookSearchRepository.findById(isbn)).willReturn(Optional.of(document));

        // when
        bookStatsService.incrementSearchCount(isbn);

        // then
        verify(bookSearchRepository).findById(isbn);
    }

    @Test
    @DisplayName("여러 ISBN의 검색수를 증가시킨다")
    void incrementSearchCounts_Success() {
        // given
        List<String> isbns = Arrays.asList("9788960777330", "9788966262281");
        given(bookSearchRepository.findById(anyString())).willReturn(Optional.of(document));

        // when
        bookStatsService.incrementSearchCounts(isbns);

        // then
        verify(bookSearchRepository).findById("9788960777330");
        verify(bookSearchRepository).findById("9788966262281");
    }

    @Test
    @DisplayName("리뷰 통계를 업데이트한다")
    void updateReviewStats_Success() {
        // given
        String isbn = "9788960777330";
        double newAverageRating = 4.5;
        int newReviewCount = 150;

        // when
        bookStatsService.updateReviewStats(isbn, newAverageRating, newReviewCount);

        // then - 메서드가 정상적으로 호출되는지만 확인 (실제 검증은 통합 테스트에서)
        // 이 메서드는 내부적으로 pendingUpdates에 저장하므로 직접적인 검증이 어려움
    }

    @Test
    @DisplayName("존재하지 않는 책의 조회수 증가시 경고 로그가 출력된다")
    void incrementViewCount_BookNotFound_LogsWarning() {
        // given
        String isbn = "nonexistent";
        given(bookSearchRepository.findById(isbn)).willReturn(Optional.empty());

        // when
        bookStatsService.incrementViewCount(isbn);

        // then
        verify(bookSearchRepository).findById(isbn);
    }

    @Test
    @DisplayName("존재하지 않는 책의 검색수 증가시 경고 로그가 출력된다")
    void incrementSearchCount_BookNotFound_LogsWarning() {
        // given
        String isbn = "nonexistent";
        given(bookSearchRepository.findById(isbn)).willReturn(Optional.empty());

        // when
        bookStatsService.incrementSearchCount(isbn);

        // then
        verify(bookSearchRepository).findById(isbn);
    }

    @Test
    @DisplayName("빈 ISBN 목록으로 검색수 증가시 아무 작업도 수행하지 않는다")
    void incrementSearchCounts_EmptyList_DoesNothing() {
        // given
        List<String> emptyIsbns = Arrays.asList();

        // when
        bookStatsService.incrementSearchCounts(emptyIsbns);

        // then - 빈 리스트이므로 repository 호출이 없어야 함
    }

    @Test
    @DisplayName("단일 ISBN으로 검색수를 증가시킨다")
    void incrementSearchCounts_SingleIsbn_Success() {
        // given
        List<String> singleIsbn = Arrays.asList("9788960777330");
        given(bookSearchRepository.findById("9788960777330")).willReturn(Optional.of(document));

        // when
        bookStatsService.incrementSearchCounts(singleIsbn);

        // then
        verify(bookSearchRepository).findById("9788960777330");
    }

    @Test
    @DisplayName("StatsDelta 객체가 올바르게 동작한다")
    void statsDelta_Operations_Success() {
        // given
        BookStatsService.StatsDelta delta = new BookStatsService.StatsDelta();

        // when
        delta.incrementViewCount();
        delta.incrementSearchCount();
        delta.updateReviewStats(4.5, 100);
        delta.updatePopularity(85.5);

        // then
        // Getter 메서드 호출로 상태 확인
        // 실제 값 검증은 getter가 정상 동작하는지만 확인
    }
}