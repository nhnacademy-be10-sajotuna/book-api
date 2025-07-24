package com.sajotuna.books.search.controller;

import com.sajotuna.books.book.exception.BookNotFoundException;
import com.sajotuna.books.category.exception.InvalidCategoryIdFormatException;
import com.sajotuna.books.search.controller.reponse.BookSearchResponse;
import com.sajotuna.books.search.service.BookSearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookSearchController.class)
@ActiveProfiles("test")
class BookSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookSearchService bookSearchService;

    @Test
    @DisplayName("키워드 없이 전체 검색을 수행한다")
    void search_WithoutKeyword_Success() throws Exception {
        // given
        BookSearchResponse response1 = new BookSearchResponse(
                "9788960777330", "클린 코드", "로버트 C. 마틴",
                31500.0, 35000.0, 4.5, "http://image.url", 85.5,
                LocalDate.of(2013, 12, 24), 100, 50
        );

        List<BookSearchResponse> responses = Arrays.asList(response1);
        Page<BookSearchResponse> page = new PageImpl<>(responses, PageRequest.of(0, 10), responses.size());

        given(bookSearchService.search(isNull(), isNull(), anyInt(), anyInt(), anyString(), any(Pageable.class)))
                .willReturn(page);

        // when & then
        mockMvc.perform(get("/api/search")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("클린 코드"));
    }

    @Test
    @DisplayName("키워드로 검색을 수행한다")
    void search_WithKeyword_Success() throws Exception {
        // given
        String keyword = "클린 코드";
        BookSearchResponse response = new BookSearchResponse(
                "9788960777330", "클린 코드", "로버트 C. 마틴",
                31500.0, 35000.0, 4.5, "http://image.url", 85.5,
                LocalDate.of(2013, 12, 24), 100, 50
        );

        List<BookSearchResponse> responses = Arrays.asList(response);
        Page<BookSearchResponse> page = new PageImpl<>(responses, PageRequest.of(0, 10), responses.size());

        given(bookSearchService.search(eq(keyword), isNull(), anyInt(), anyInt(), anyString(), any(Pageable.class)))
                .willReturn(page);

        // when & then
        mockMvc.perform(get("/api/search")
                        .param("keyword", keyword)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("클린 코드"));
    }

    @Test
    @DisplayName("카테고리로 검색을 수행한다")
    void search_WithCategory_Success() throws Exception {
        // given
        String category = "1";
        BookSearchResponse response = new BookSearchResponse(
                "9788960777330", "클린 코드", "로버트 C. 마틴",
                31500.0, 35000.0, 4.5, "http://image.url", 85.5,
                LocalDate.of(2013, 12, 24), 100, 50
        );

        List<BookSearchResponse> responses = Arrays.asList(response);
        Page<BookSearchResponse> page = new PageImpl<>(responses, PageRequest.of(0, 10), responses.size());

        given(bookSearchService.search(isNull(), eq(category), anyInt(), anyInt(), anyString(), any(Pageable.class)))
                .willReturn(page);

        // when & then
        mockMvc.perform(get("/api/search")
                        .param("category", category)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("클린 코드"));
    }

    @Test
    @DisplayName("잘못된 카테고리 ID로 검색시 400 오류가 발생한다")
    void search_WithInvalidCategory_BadRequest() throws Exception {
        // given
        String category = "invalid";
        given(bookSearchService.search(isNull(), eq(category), anyInt(), anyInt(), anyString(), any(Pageable.class)))
                .willThrow(new InvalidCategoryIdFormatException(category));

        // when & then
        mockMvc.perform(get("/api/search")
                        .param("category", category))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("제목 자동완성을 수행한다")
    void autoComplete_Success() throws Exception {
        // given
        String keyword = "클린";
        List<String> suggestions = Arrays.asList("클린 코드", "클린 아키텍처");

        given(bookSearchService.autoCompleteTitle(keyword)).willReturn(suggestions);

        // when & then
        mockMvc.perform(get("/api/search/autocomplete")
                        .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0]").value("클린 코드"))
                .andExpect(jsonPath("$[1]").value("클린 아키텍처"));
    }

    @Test
    @DisplayName("키워드 파라미터 없이 자동완성 요청시 오류가 발생한다")
    void autoComplete_NoKeywordParam_Error() throws Exception {
        // when & then
        mockMvc.perform(get("/api/search/autocomplete"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("ISBN으로 책을 검색한다")
    void searchByIsbn_Success() throws Exception {
        // given
        String isbn = "9788960777330";
        BookSearchResponse response = new BookSearchResponse(
                isbn, "클린 코드", "로버트 C. 마틴",
                31500.0, 35000.0, 4.5, "http://image.url", 85.5,
                LocalDate.of(2013, 12, 24), 100, 50
        );

        given(bookSearchService.searchByIsbn(isbn)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/search/isbn")
                        .param("isbn", isbn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value(isbn))
                .andExpect(jsonPath("$.title").value("클린 코드"));
    }

    @Test
    @DisplayName("존재하지 않는 ISBN으로 검색시 404 오류가 발생한다")
    void searchByIsbn_NotFound_NotFound() throws Exception {
        // given
        String isbn = "nonexistent";
        given(bookSearchService.searchByIsbn(isbn)).willThrow(new BookNotFoundException(isbn));

        // when & then
        mockMvc.perform(get("/api/search/isbn")
                        .param("isbn", isbn))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("ISBN 파라미터 없이 ISBN 검색 요청시 오류가 발생한다")
    void searchByIsbn_NoIsbnParam_Error() throws Exception {
        // when & then
        mockMvc.perform(get("/api/search/isbn"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("검색 결과가 없는 경우 빈 페이지를 반환한다")
    void search_NoResults_ReturnsEmptyPage() throws Exception {
        // given
        String keyword = "존재하지않는책";
        Page<BookSearchResponse> emptyPage = new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0);

        given(bookSearchService.search(eq(keyword), isNull(), anyInt(), anyInt(), anyString(), any(Pageable.class)))
                .willReturn(emptyPage);

        // when & then
        mockMvc.perform(get("/api/search")
                        .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));
    }
}