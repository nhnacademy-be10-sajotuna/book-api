package com.sajotuna.books.tag.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.books.tag.controller.request.TagRequest;
import com.sajotuna.books.tag.controller.response.TagResponse;
import com.sajotuna.books.tag.domain.Tag;
import com.sajotuna.books.tag.exception.TagNotFoundException;
import com.sajotuna.books.tag.service.TagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TagController.class)
@ActiveProfiles("test")
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TagService tagService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("모든 태그를 페이지네이션으로 조회한다")
    void getAllTags_Success() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        TagResponse tag1 = new TagResponse(1L, "자바");
        TagResponse tag2 = new TagResponse(2L, "스프링");
        List<TagResponse> tags = Arrays.asList(tag1, tag2);
        Page<TagResponse> pagedTags = new PageImpl<>(tags, pageable, tags.size());

        given(tagService.getAllTags(any(Pageable.class))).willReturn(pagedTags);

        // when & then
        mockMvc.perform(get("/api/tags")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].tagName").value("자바"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].tagName").value("스프링"));
    }

    @Test
    @DisplayName("ID로 특정 태그를 조회한다")
    void getTagById_Success() throws Exception {
        // given
        Long tagId = 1L;
        Tag tag = new Tag("자바");
        tag.setId(tagId);

        given(tagService.getTagById(anyLong())).willReturn(Optional.of(tag));

        // when & then
        mockMvc.perform(get("/api/tags/{id}", tagId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tagId))
                .andExpect(jsonPath("$.tagName").value("자바"));
    }

    @Test
    @DisplayName("존재하지 않는 태그 조회 시 404를 반환한다")
    void getTagById_NotFound() throws Exception {
        // given
        Long tagId = 999L;
        given(tagService.getTagById(anyLong())).willReturn(Optional.empty());

        // when & then
        mockMvc.perform(get("/api/tags/{id}", tagId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("새로운 태그를 생성한다")
    void createTag_Success() throws Exception {
        // given
        TagRequest request = new TagRequest("새태그");
        Tag savedTag = new Tag("새태그");
        savedTag.setId(1L);

        given(tagService.createTag(anyString())).willReturn(savedTag);

        // when & then
        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tagName").value("새태그"));
    }

    @Test
    @DisplayName("빈 태그명으로 생성 시 검증 오류가 발생한다")
    void createTag_EmptyTagName_ValidationError() throws Exception {
        // given
        TagRequest request = new TagRequest("");

        // when & then
        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("기존 태그를 수정한다")
    void updateTag_Success() throws Exception {
        // given
        Long tagId = 1L;
        TagRequest request = new TagRequest("수정된태그");
        
        Tag existingTag = new Tag("기존태그");
        existingTag.setId(tagId);
        
        Tag updatedTag = new Tag("수정된태그");
        updatedTag.setId(tagId);

        given(tagService.getTagById(anyLong())).willReturn(Optional.of(existingTag));
        given(tagService.updateTag(anyLong(), anyString())).willReturn(updatedTag);

        // when & then
        mockMvc.perform(put("/api/tags/{id}", tagId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tagId))
                .andExpect(jsonPath("$.tagName").value("수정된태그"));
    }

    @Test
    @DisplayName("존재하지 않는 태그 수정 시 예외가 발생한다")
    void updateTag_NotFound_ThrowsException() throws Exception {
        // given
        Long tagId = 999L;
        TagRequest request = new TagRequest("수정된태그");

        given(tagService.getTagById(anyLong())).willReturn(Optional.empty());

        // when & then
        mockMvc.perform(put("/api/tags/{id}", tagId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("태그를 삭제한다")
    void deleteTag_Success() throws Exception {
        // given
        Long tagId = 1L;
        given(tagService.deleteTag(anyLong())).willReturn(true);

        // when & then
        mockMvc.perform(delete("/api/tags/{id}", tagId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("존재하지 않는 태그 삭제 시 예외가 발생한다")
    void deleteTag_NotFound_ThrowsException() throws Exception {
        // given
        Long tagId = 999L;
        given(tagService.deleteTag(anyLong())).willReturn(false);

        // when & then
        mockMvc.perform(delete("/api/tags/{id}", tagId))
                .andExpect(status().isNotFound());
    }
}