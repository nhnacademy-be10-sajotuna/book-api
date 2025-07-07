package com.sajotuna.books.tag.service.impl;

import com.sajotuna.books.tag.controller.response.TagResponse;
import com.sajotuna.books.tag.exception.InvalidTagNameException;
import com.sajotuna.books.tag.exception.TagAlreadyExistsException;
import com.sajotuna.books.tag.exception.TagNotFoundException;
import com.sajotuna.books.tag.domain.Tag;
import com.sajotuna.books.tag.repository.TagRepository;
import com.sajotuna.books.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public Set<Tag> findOrCreateTags(Set<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        for (String name : tagNames) {
            Tag tag = tagRepository.findByTagName(name)
                    .orElseGet(() -> tagRepository.save(new Tag(name)));
            tags.add(tag);
        }

        return tags;
    }


    @Override
    public Optional<Tag> getTagById(Long id) {
        return tagRepository.findById(id);
    }

    @Override
    public Tag createTag(String tagName) {

        if (tagName == null || tagName.trim().isEmpty()) {
            throw new InvalidTagNameException();
        }
        if (tagRepository.existsByTagName(tagName)) {
            throw new TagAlreadyExistsException(tagName);
        }
        Tag entity = new Tag(tagName);
        return tagRepository.save(entity);
    }

    @Override
    public Tag updateTag(Long id, String newTagName) {
        if (newTagName == null || newTagName.trim().isEmpty()) {
            throw new InvalidTagNameException();
        }

        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));
        tag.setTagName(newTagName);
        return tagRepository.save(tag);
    }


    @Override
    public boolean deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new TagNotFoundException(id);
        }
        // Tag 엔티티의 bookTags 필드에 orphanRemoval = true 설정되어 있으므로
        // Tag 삭제 시 연결된 BookTag 엔티티들도 자동으로 삭제되어 도서와의 연결이 해제됩니다.
        tagRepository.deleteById(id);
        return true;
    }

    @Override
    public Page<TagResponse> getAllTags(Pageable pageable) {
        Page<Tag> tagPage = tagRepository.findAll(pageable);
        return tagPage.map(TagResponse::from);
    }

}