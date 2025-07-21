package com.sajotuna.books.search.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookSearchSynService {

    private final BookStatsService bookStatsService;

    public void updateSearchStats(List<String> isbns) {
        // ES 전용 통계 업데이트로 변경
        bookStatsService.incrementSearchCounts(isbns);
    }
}
