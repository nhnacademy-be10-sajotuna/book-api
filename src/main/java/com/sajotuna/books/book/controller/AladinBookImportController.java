package com.sajotuna.books.book.controller;

import com.sajotuna.books.book.controller.response.AladinBookResponse;
import com.sajotuna.books.book.service.AladinBookImportService;
import com.sajotuna.books.book.service.AladinFetchService;
import com.sajotuna.books.book.service.AladinStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/search")
public class AladinBookImportController {


    private final AladinFetchService aladinFetchService;
    private final AladinBookImportService aladinBookImportService;

    @PostMapping("/import")
    public ResponseEntity<Void> importBooks(@RequestParam String keyword, @RequestParam int totalPages) {
        List<AladinBookResponse> books = aladinFetchService.fetchBooks(keyword, totalPages);
        aladinBookImportService.importBooks(books);
        return ResponseEntity.ok().build();
    }

}
