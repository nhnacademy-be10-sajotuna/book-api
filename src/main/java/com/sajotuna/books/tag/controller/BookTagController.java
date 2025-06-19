package com.sajotuna.books.tag.controller;

import com.sajotuna.books.tag.controller.request.BookTagRequest;
import com.sajotuna.books.tag.controller.response.BookTagResponse;
import com.sajotuna.books.tag.service.BookTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookTags")
@RequiredArgsConstructor
public class BookTagController {

   private final BookTagService bookTagService;

   //책에 태그 추가
   @PostMapping
    public ResponseEntity<BookTagResponse> addTagToBook(@RequestBody BookTagRequest request){
       return ResponseEntity.ok(bookTagService.addTagToBook(request));
   }

   //책에서 태그 제거
   @DeleteMapping
    public ResponseEntity<BookTagResponse> removeTagFromBook(@RequestBody BookTagRequest request){
       bookTagService.removeTagFromBook(request);
       return ResponseEntity.noContent().build();
   }

    @GetMapping("/{isbn}")
    public ResponseEntity<List<BookTagResponse>> getTagsByBook(@PathVariable String isbn) {
        return ResponseEntity.ok(bookTagService.getTagsByBook(isbn));
    }




}
