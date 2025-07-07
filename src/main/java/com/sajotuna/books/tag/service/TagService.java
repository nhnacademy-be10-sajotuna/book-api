package com.sajotuna.books.tag.service;

import com.sajotuna.books.tag.controller.response.TagResponse;
import com.sajotuna.books.tag.domain.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagService {

    Set<Tag> findOrCreateTags(Set<String> tagNames);

    Optional<Tag> getTagById(Long id);
    Tag createTag(String tagName);
    Tag updateTag(Long id, String newTagName);
    // 태그가 삭제가 되면 해당 태그가 연결된 도서들의 연결도 같이 삭제가 되어야 한다.
    boolean deleteTag(Long id);
    Page<TagResponse> getAllTags(Pageable pageable);

}