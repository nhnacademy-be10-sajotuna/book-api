package com.sajotuna.books.controller;

import com.sajotuna.books.dto.BookResponse;
import com.sajotuna.books.dto.SearchRequest;
import com.sajotuna.books.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 도서 검색 API
     * GET /api/search
     * Query Params:
     * keyword: 검색 키워드
     * sortBy: 정렬 기준 (popularity, new_arrivals, lowest_price, highest_price, rating, reviews)
     */
    @GetMapping
    public ResponseEntity<List<BookResponse>> searchBooks(@ModelAttribute SearchRequest searchRequest) {
        List<BookResponse> results = searchService.searchBooks(searchRequest);
        if (results.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(results);
    }
}