package com.sajotuna.books.search.controller;

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

    @GetMapping
    public Page<BookSearchResponse> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "popularity") String sort,
            Pageable pageable
    ){
        return bookSearchService.search(keyword,category,pageable.getPageNumber(),pageable.getPageSize(),sort,pageable);
    }

//    @GetMapping("/categories")
//    public Page<BookSearchResponse> searchByCategory(
//            @RequestParam(required = false) String category,
//            @RequestParam(defaultValue = "popularity") String sort,
//            Pageable pageable
//    ){
//        return bookSearchService.searchByCategoryId(category,pageable.getPageNumber(),pageable.getPageSize(),sort,pageable);
//    }

    @GetMapping("/autocomplete")
    public List<String> autoComplete(@RequestParam String keyword){
        return bookSearchService.autoCompleteTitle(keyword);
    }



}
