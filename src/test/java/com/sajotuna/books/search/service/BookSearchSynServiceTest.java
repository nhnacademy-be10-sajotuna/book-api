package com.sajotuna.books.search.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class BookSearchSynServiceTest {

    @Mock
    private BookStatsService bookStatsService;

    @InjectMocks
    private BookSearchSynService bookSearchSynService;

    @Test
    @DisplayName("검색 통계를 업데이트한다")
    void updateSearchStats_Success() {
        // given
        List<String> isbns = Arrays.asList("9788960777330", "9788966262281");

        // when
        bookSearchSynService.updateSearchStats(isbns);

        // then
        verify(bookStatsService).incrementSearchCounts(isbns);
    }

    @Test
    @DisplayName("빈 ISBN 목록으로 검색 통계를 업데이트한다")
    void updateSearchStats_EmptyList_Success() {
        // given
        List<String> emptyIsbns = Arrays.asList();

        // when
        bookSearchSynService.updateSearchStats(emptyIsbns);

        // then
        verify(bookStatsService).incrementSearchCounts(emptyIsbns);
    }

    @Test
    @DisplayName("단일 ISBN으로 검색 통계를 업데이트한다")
    void updateSearchStats_SingleIsbn_Success() {
        // given
        List<String> singleIsbn = Arrays.asList("9788960777330");

        // when
        bookSearchSynService.updateSearchStats(singleIsbn);

        // then
        verify(bookStatsService).incrementSearchCounts(singleIsbn);
    }
}