package com.sajotuna.books.search.controller;

import com.sajotuna.books.book.service.BookService;
import com.sajotuna.books.search.controller.reponse.BookSearchResponse;
import com.sajotuna.books.search.service.BookSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class BookSearchController {

    private final BookSearchService bookSearchService;

    @GetMapping("/books")
    public Page<BookSearchResponse> searchBooks(
            @RequestParam String keyword,
            @RequestParam(required = false) Long categoryId, // 새롭게 추가된 부분
            @RequestParam(defaultValue = "popularity") String sort,
            Pageable pageable
    ){
        return bookSearchService.search(keyword, pageable.getPageNumber(), pageable.getPageSize(), sort, categoryId, pageable); // categoryId 파라미터 전달
    }
}