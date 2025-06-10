// src/main/java/com/sajotuna/books/service/impl/TagServiceImpl.java
package com.sajotuna.books.service.impl;

import com.sajotuna.books.dto.TagRequest;
import com.sajotuna.books.dto.TagResponse;
import com.sajotuna.books.model.Tag;
import com.sajotuna.books.repository.TagRepository;
import com.sajotuna.books.service.TagService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public TagResponse createTag(TagRequest tagRequest) {
        // 이미 존재하는 태그인지 확인
        Optional<Tag> existingTag = tagRepository.findByName(tagRequest.getName());
        if (existingTag.isPresent()) {
            // 이미 존재하는 태그라면 기존 태그 정보 반환 (또는 예외 발생)
            return new TagResponse(existingTag.get());
        }

        Tag tag = new Tag(tagRequest.getName());
        Tag savedTag = tagRepository.save(tag);
        return new TagResponse(savedTag);
    }

    @Override
    public List<TagResponse> getAllTags() {
        return tagRepository.findAll().stream()
                .map(TagResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public TagResponse getTagById(Long id) {
        return tagRepository.findById(id)
                .map(TagResponse::new)
                .orElse(null); // 태그가 없으면 null 반환
    }

    @Override
    public TagResponse getTagByName(String name) {
        return tagRepository.findByName(name)
                .map(TagResponse::new)
                .orElse(null);
    }

    @Override
    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }
}