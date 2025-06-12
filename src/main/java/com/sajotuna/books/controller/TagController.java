package com.sajotuna.books.controller;

import com.sajotuna.books.dto.TagRequest;
import com.sajotuna.books.dto.TagResponse;
import com.sajotuna.books.exception.TagAlreadyExistsException;
import com.sajotuna.books.exception.TagNotFoundException;
import com.sajotuna.books.model.Tag;

import com.sajotuna.books.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

   private final TagService tagService;

   //모든 태그 조회
   @GetMapping
   public ResponseEntity<List<TagResponse>> getAllTags() {
       List<TagResponse> tags = tagService.getAllTags().stream()
               .map(TagResponse::from)
               .collect(Collectors.toList());
       return ResponseEntity.ok(tags);
   }

   //특정 태그 조회
    @GetMapping("/{id}")
    public ResponseEntity<TagResponse> getTagById(@PathVariable long id) {
       return tagService.getTagById(id)
               .map(TagResponse::from)
               .map(ResponseEntity::ok)
               .orElse(ResponseEntity.notFound().build());
    }

    //새 태그 생성
    @PostMapping
    public ResponseEntity<TagResponse> createTag(@RequestBody @Valid TagRequest request) {

        Tag saved = tagService.createTag(request.tagName());
        return ResponseEntity.ok(TagResponse.from(saved));
    }

    //기존 태그 수정
    @PutMapping("/{id}")
    public ResponseEntity<TagResponse> updateTag(@PathVariable Long id, @RequestBody @Valid TagRequest request) {
        tagService.getTagById(id).orElseThrow(() -> new TagNotFoundException(id));
        Tag updated = tagService.updateTag(id, request.tagName());
        return ResponseEntity.ok(TagResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        if (!tagService.deleteTag(id)) {
            throw new TagNotFoundException(id);
        }
        return ResponseEntity.noContent().build();
    }


}
