package com.sajotuna.books.book.controller;

import com.sajotuna.books.book.service.AladinBookImportService;
import com.sajotuna.books.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/search")
public class AladinBookImportController {

    private final AladinBookImportService importService;

    @PostMapping("/import-by-keyword")
    public ResponseEntity<String> importByKeword(@RequestParam String keyword, @RequestParam int page) {
        importService.importBooksByKeyword(keyword, page);
        return ResponseEntity.ok("키워드 '" + keyword + "'로 도서 수집 완료");

    }

}
