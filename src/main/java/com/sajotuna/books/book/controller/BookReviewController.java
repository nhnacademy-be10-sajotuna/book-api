package com.sajotuna.books.book.controller;

import com.sajotuna.books.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books/{isbn}")
@RequiredArgsConstructor
public class BookReviewController {

    private final BookService bookService;

    @PostMapping("/review")
    public void addReview(@PathVariable String isbn, @RequestBody double rating){
        bookService.updateReviewInfo(isbn, rating);
    }


}