package com.sajotuna.books.book.controller;

import com.sajotuna.books.book.controller.response.AladinBookResponse;
import com.sajotuna.books.book.service.AladinBookImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/search")
public class AladinBookImportController {

    private final AladinBookImportService importService;
    private final AladinBookImportService aladinBookImportService;

    @PostMapping("/import-by-keyword")
    public ResponseEntity<String> importByKeword(@RequestParam String keyword, @RequestParam int page) {
        importService.importBooksByKeyword(keyword, page);
        return ResponseEntity.ok("키워드 '" + keyword + "'로 도서 수집 완료");

    }




}
