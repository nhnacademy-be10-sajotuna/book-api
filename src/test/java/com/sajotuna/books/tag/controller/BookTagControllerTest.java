package com.sajotuna.books.tag.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.books.tag.controller.request.BookTagRequest;
import com.sajotuna.books.tag.controller.response.BookTagResponse;
import com.sajotuna.books.tag.service.BookTagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookTagController.class)
@ActiveProfiles("test")
class BookTagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookTagService bookTagService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("책에 태그를 추가한다")
    void addTagToBook_Success() throws Exception {
        // given
        BookTagRequest request = new BookTagRequest("9788960777330", 1L);
        BookTagResponse response = new BookTagResponse(1L, 1L, "자바", "9788960777330");

        given(bookTagService.addTagToBook(any(BookTagRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/bookTags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tagId").value(1))
                .andExpect(jsonPath("$.tagName").value("자바"))
                .andExpect(jsonPath("$.isbn").value("9788960777330"));
    }

    @Test
    @DisplayName("책에서 태그를 제거한다")
    void removeTagFromBook_Success() throws Exception {
        // given
        BookTagRequest request = new BookTagRequest("9788960777330", 1L);
        doNothing().when(bookTagService).removeTagFromBook(any(BookTagRequest.class));

        // when & then
        mockMvc.perform(delete("/api/bookTags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("책의 모든 태그를 조회한다")
    void getTagsByBook_Success() throws Exception {
        // given
        String isbn = "9788960777330";
        List<BookTagResponse> tags = Arrays.asList(
                new BookTagResponse(1L, 1L, "자바", isbn),
                new BookTagResponse(2L, 2L, "스프링", isbn),
                new BookTagResponse(3L, 3L, "프로그래밍", isbn)
        );

        given(bookTagService.getTagsByBook(anyString())).willReturn(tags);

        // when & then
        mockMvc.perform(get("/api/bookTags/{isbn}", isbn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].tagName").value("자바"))
                .andExpect(jsonPath("$[0].isbn").value(isbn))
                .andExpect(jsonPath("$[1].tagName").value("스프링"))
                .andExpect(jsonPath("$[2].tagName").value("프로그래밍"));
    }

    @Test
    @DisplayName("태그가 없는 책 조회 시 빈 배열을 반환한다")
    void getTagsByBook_EmptyTags_Success() throws Exception {
        // given
        String isbn = "9788966262281";
        List<BookTagResponse> emptyTags = Arrays.asList();

        given(bookTagService.getTagsByBook(anyString())).willReturn(emptyTags);

        // when & then
        mockMvc.perform(get("/api/bookTags/{isbn}", isbn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("여러 태그를 가진 책에 새로운 태그를 추가한다")
    void addTagToBook_MultipleExistingTags_Success() throws Exception {
        // given
        BookTagRequest request = new BookTagRequest("9788960777330", 4L);
        BookTagResponse response = new BookTagResponse(4L, 4L, "백엔드", "9788960777330");

        given(bookTagService.addTagToBook(any(BookTagRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/bookTags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tagName").value("백엔드"));
    }

    @Test
    @DisplayName("책에서 특정 태그를 제거한다")
    void removeSpecificTagFromBook_Success() throws Exception {
        // given
        BookTagRequest request = new BookTagRequest("9788960777330", 2L);
        doNothing().when(bookTagService).removeTagFromBook(any(BookTagRequest.class));

        // when & then
        mockMvc.perform(delete("/api/bookTags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }
}