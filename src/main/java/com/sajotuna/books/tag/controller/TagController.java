package com.sajotuna.books.tag.controller;

import com.sajotuna.books.tag.controller.request.TagRequest;
import com.sajotuna.books.tag.controller.response.TagResponse;
import com.sajotuna.books.tag.exception.TagNotFoundException;
import com.sajotuna.books.tag.domain.Tag;

import com.sajotuna.books.tag.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

   private final TagService tagService;

   //모든 태그 조회
   @GetMapping
   public ResponseEntity<Page<TagResponse>> getAllTags(Pageable pageable) {
       return ResponseEntity.ok(tagService.getAllTags(pageable));

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
