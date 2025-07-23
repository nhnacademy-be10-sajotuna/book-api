package com.sajotuna.books.tag.service.impl;

import com.sajotuna.books.tag.controller.response.TagResponse;
import com.sajotuna.books.tag.domain.Tag;
import com.sajotuna.books.tag.exception.InvalidTagNameException;
import com.sajotuna.books.tag.exception.TagAlreadyExistsException;
import com.sajotuna.books.tag.exception.TagNotFoundException;
import com.sajotuna.books.tag.repository.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    @Test
    @DisplayName("기존 태그들을 찾거나 새로 생성한다")
    void findOrCreateTags_Success() {
        // given
        Set<String> tagNames = Set.of("자바", "스프링", "새태그");
        
        Tag existingTag1 = new Tag("자바");
        existingTag1.setId(1L);
        Tag existingTag2 = new Tag("스프링");
        existingTag2.setId(2L);
        Tag newTag = new Tag("새태그");
        newTag.setId(3L);

        given(tagRepository.findByTagName("자바")).willReturn(Optional.of(existingTag1));
        given(tagRepository.findByTagName("스프링")).willReturn(Optional.of(existingTag2));
        given(tagRepository.findByTagName("새태그")).willReturn(Optional.empty());
        given(tagRepository.save(any(Tag.class))).willReturn(newTag);

        // when
        Set<Tag> result = tagService.findOrCreateTags(tagNames);

        // then
        assertThat(result).hasSize(3);
        assertThat(result).extracting(Tag::getTagName)
                .containsExactlyInAnyOrder("자바", "스프링", "새태그");
        
        verify(tagRepository).findByTagName("자바");
        verify(tagRepository).findByTagName("스프링");
        verify(tagRepository).findByTagName("새태그");
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    @DisplayName("ID로 태그를 조회한다")
    void getTagById_Success() {
        // given
        Long tagId = 1L;
        Tag tag = new Tag("자바");
        tag.setId(tagId);
        
        given(tagRepository.findById(tagId)).willReturn(Optional.of(tag));

        // when
        Optional<Tag> result = tagService.getTagById(tagId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(tagId);
        assertThat(result.get().getTagName()).isEqualTo("자바");
    }

    @Test
    @DisplayName("존재하지 않는 ID로 태그 조회 시 빈 Optional을 반환한다")
    void getTagById_NotFound_ReturnsEmpty() {
        // given
        Long tagId = 999L;
        given(tagRepository.findById(tagId)).willReturn(Optional.empty());

        // when
        Optional<Tag> result = tagService.getTagById(tagId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("새로운 태그를 생성한다")
    void createTag_Success() {
        // given
        String tagName = "새태그";
        Tag savedTag = new Tag(tagName);
        savedTag.setId(1L);

        given(tagRepository.existsByTagName(tagName)).willReturn(false);
        given(tagRepository.save(any(Tag.class))).willReturn(savedTag);

        // when
        Tag result = tagService.createTag(tagName);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTagName()).isEqualTo(tagName);
        
        verify(tagRepository).existsByTagName(tagName);
        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    @DisplayName("null 태그명으로 생성 시 예외가 발생한다")
    void createTag_NullTagName_ThrowsException() {
        // given
        String tagName = null;

        // when & then
        assertThatThrownBy(() -> tagService.createTag(tagName))
                .isInstanceOf(InvalidTagNameException.class);
        
        verify(tagRepository, never()).save(any());
    }

    @Test
    @DisplayName("빈 태그명으로 생성 시 예외가 발생한다")
    void createTag_EmptyTagName_ThrowsException() {
        // given
        String tagName = "";

        // when & then
        assertThatThrownBy(() -> tagService.createTag(tagName))
                .isInstanceOf(InvalidTagNameException.class);
        
        verify(tagRepository, never()).save(any());
    }

    @Test
    @DisplayName("공백만 있는 태그명으로 생성 시 예외가 발생한다")
    void createTag_WhitespaceTagName_ThrowsException() {
        // given
        String tagName = "   ";

        // when & then
        assertThatThrownBy(() -> tagService.createTag(tagName))
                .isInstanceOf(InvalidTagNameException.class);
        
        verify(tagRepository, never()).save(any());
    }

    @Test
    @DisplayName("이미 존재하는 태그명으로 생성 시 예외가 발생한다")
    void createTag_AlreadyExists_ThrowsException() {
        // given
        String tagName = "기존태그";
        given(tagRepository.existsByTagName(tagName)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> tagService.createTag(tagName))
                .isInstanceOf(TagAlreadyExistsException.class);
        
        verify(tagRepository).existsByTagName(tagName);
        verify(tagRepository, never()).save(any());
    }

    @Test
    @DisplayName("태그를 수정한다")
    void updateTag_Success() {
        // given
        Long tagId = 1L;
        String newTagName = "수정된태그";
        Tag existingTag = new Tag("기존태그");
        existingTag.setId(tagId);
        Tag updatedTag = new Tag(newTagName);
        updatedTag.setId(tagId);

        given(tagRepository.findById(tagId)).willReturn(Optional.of(existingTag));
        given(tagRepository.save(existingTag)).willReturn(updatedTag);

        // when
        Tag result = tagService.updateTag(tagId, newTagName);

        // then
        assertThat(result.getId()).isEqualTo(tagId);
        assertThat(result.getTagName()).isEqualTo(newTagName);
        
        verify(tagRepository).findById(tagId);
        verify(tagRepository).save(existingTag);
    }

    @Test
    @DisplayName("존재하지 않는 태그 수정 시 예외가 발생한다")
    void updateTag_NotFound_ThrowsException() {
        // given
        Long tagId = 999L;
        String newTagName = "수정된태그";
        given(tagRepository.findById(tagId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> tagService.updateTag(tagId, newTagName))
                .isInstanceOf(TagNotFoundException.class);
        
        verify(tagRepository).findById(tagId);
        verify(tagRepository, never()).save(any());
    }

    @Test
    @DisplayName("null 태그명으로 수정 시 예외가 발생한다")
    void updateTag_NullTagName_ThrowsException() {
        // given
        Long tagId = 1L;
        String newTagName = null;

        // when & then
        assertThatThrownBy(() -> tagService.updateTag(tagId, newTagName))
                .isInstanceOf(InvalidTagNameException.class);
        
        verify(tagRepository, never()).findById(any());
        verify(tagRepository, never()).save(any());
    }

    @Test
    @DisplayName("태그를 삭제한다")
    void deleteTag_Success() {
        // given
        Long tagId = 1L;
        given(tagRepository.existsById(tagId)).willReturn(true);

        // when
        boolean result = tagService.deleteTag(tagId);

        // then
        assertThat(result).isTrue();
        
        verify(tagRepository).existsById(tagId);
        verify(tagRepository).deleteById(tagId);
    }

    @Test
    @DisplayName("존재하지 않는 태그 삭제 시 예외가 발생한다")
    void deleteTag_NotFound_ThrowsException() {
        // given
        Long tagId = 999L;
        given(tagRepository.existsById(tagId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> tagService.deleteTag(tagId))
                .isInstanceOf(TagNotFoundException.class);
        
        verify(tagRepository).existsById(tagId);
        verify(tagRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("모든 태그를 페이지네이션으로 조회한다")
    void getAllTags_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Tag tag1 = new Tag("자바");
        tag1.setId(1L);
        Tag tag2 = new Tag("스프링");
        tag2.setId(2L);
        
        List<Tag> tags = Arrays.asList(tag1, tag2);
        Page<Tag> tagPage = new PageImpl<>(tags, pageable, tags.size());
        
        given(tagRepository.findAll(pageable)).willReturn(tagPage);

        // when
        Page<TagResponse> result = tagService.getAllTags(pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting(TagResponse::tagName)
                .containsExactly("자바", "스프링");
        assertThat(result.getTotalElements()).isEqualTo(2);
        
        verify(tagRepository).findAll(pageable);
    }

    @Test
    @DisplayName("빈 태그명 Set으로 findOrCreateTags 호출 시 빈 Set을 반환한다")
    void findOrCreateTags_EmptySet_ReturnsEmptySet() {
        // given
        Set<String> emptyTagNames = Set.of();

        // when
        Set<Tag> result = tagService.findOrCreateTags(emptyTagNames);

        // then
        assertThat(result).isEmpty();
        verify(tagRepository, never()).findByTagName(anyString());
        verify(tagRepository, never()).save(any());
    }
}