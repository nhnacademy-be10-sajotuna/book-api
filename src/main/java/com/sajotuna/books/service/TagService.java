// src/main/java/com/sajotuna/books/service/TagService.java
package com.sajotuna.books.service;

import com.sajotuna.books.dto.TagRequest;
import com.sajotuna.books.dto.TagResponse;

import java.util.List;

public interface TagService {
    TagResponse createTag(TagRequest tagRequest);
    List<TagResponse> getAllTags();
    TagResponse getTagById(Long id);
    TagResponse getTagByName(String name); // 이름으로 태그 조회 추가
    void deleteTag(Long id);
}