package com.sajotuna.books.tag.service;

import com.sajotuna.books.tag.controller.response.TagResponse;
import com.sajotuna.books.tag.domain.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagService {

    // 도서등록시 여러태그 한꺼번에 등록 or 재사용?????
    Set<Tag> findOrCreateTags(Set<String> tagNames);

    List<Tag> getAllTags();
    Optional<Tag> getTagById(Long id);
    Tag createTag(String tagName);
    Tag updateTag(Long id, String newTagName);
    boolean deleteTag(Long id);
    Page<TagResponse> getAllTags(Pageable pageable);

}

