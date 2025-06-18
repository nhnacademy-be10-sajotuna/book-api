package com.sajotuna.books.controller;

import com.sajotuna.books.service.AladinBookImportService;
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

//    @PostMapping("/import-by-category")
//    public ResponseEntity<String> importByCategory(
//            @RequestParam Integer categoryId,
//            @RequestParam int page
//    ) {
//        importService.importBooksByCategoryId(categoryId, page);
//        return ResponseEntity.ok(" 카테고리 '" + categoryId + "' 도서 수집 완료");
//    }
}
