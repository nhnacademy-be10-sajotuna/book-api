package com.sajotuna.books.service.impl;

import com.sajotuna.books.exception.InvalidTagNameException;
import com.sajotuna.books.exception.TagAlreadyExistsException;
import com.sajotuna.books.exception.TagNotFoundException;
import com.sajotuna.books.model.Tag;
import com.sajotuna.books.repository.TagRepository;
import com.sajotuna.books.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    /**
     * 도서 등록 시 사용되는 내부 전용 메서드
     * 전달된 태그 이름들 중 이미 존재하는 태그는 재사용하고,
     * 존재하지 않는 태그는 새로 생성해서 모두 반환합니다.
     */
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

    /**
     * 전체 태그 목록 조회
     */
    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    /**
     * 특정 태그 ID로 조회
     */
    @Override
    public Optional<Tag> getTagById(Long id) {
        return tagRepository.findById(id);
    }

    /**
     * 새 태그 등록
     */
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
    /**
     * 기존 태그 이름 수정
     */
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


    /**
     * 태그 삭제
     */
    @Override
    public boolean deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new TagNotFoundException(id);
        }
        tagRepository.deleteById(id);
        return true;
    }
}
