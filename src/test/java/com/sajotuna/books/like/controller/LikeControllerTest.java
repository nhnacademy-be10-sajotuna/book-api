package com.sajotuna.books.like.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.books.book.controller.response.BookResponse;
import com.sajotuna.books.like.controller.request.LikeRequest;
import com.sajotuna.books.like.controller.response.LikeResponse;
import com.sajotuna.books.like.service.LikeService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LikeController.class)
@ActiveProfiles("test")
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LikeService likeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("좋아요를 추가한다")
    void addLike_Success() throws Exception {
        // given
        Long userId = 1L;
        LikeRequest request = new LikeRequest();
        request.setBookIsbn("9788960777330");
        
        LikeResponse response = new LikeResponse();
        response.setId(1L);
        response.setUserId(userId);
        response.setBookIsbn("9788960777330");
        response.setBookTitle("클린 코드");

        given(likeService.addLike(anyLong(), any(LikeRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/likes")
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.bookIsbn").value("9788960777330"))
                .andExpect(jsonPath("$.bookTitle").value("클린 코드"));
    }

    @Test
    @DisplayName("빈 bookIsbn으로 좋아요 추가 시 유효성 검증 오류가 발생한다")
    void addLike_EmptyBookIsbn_ValidationError() throws Exception {
        // given
        Long userId = 1L;
        LikeRequest request = new LikeRequest();
        request.setBookIsbn("");

        // when & then
        mockMvc.perform(post("/api/likes")
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("User-Id 헤더 없이 좋아요 추가 시 오류가 발생한다")
    void addLike_NoUserIdHeader_Error() throws Exception {
        // given
        LikeRequest request = new LikeRequest();
        request.setBookIsbn("9788960777330");

        // when & then
        mockMvc.perform(post("/api/likes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("좋아요를 제거한다")
    void removeLike_Success() throws Exception {
        // given
        Long userId = 1L;
        String bookIsbn = "9788960777330";
        
        doNothing().when(likeService).removeLike(anyLong(), anyString());

        // when & then
        mockMvc.perform(delete("/api/likes")
                        .header("X-User-Id", userId)
                        .param("bookIsbn", bookIsbn))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("bookIsbn 파라미터 없이 좋아요 제거 시 오류가 발생한다")
    void removeLike_NoBookIsbnParam_Error() throws Exception {
        // given
        Long userId = 1L;

        // when & then
        mockMvc.perform(delete("/api/likes")
                        .header("X-User-Id", userId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("사용자가 좋아요한 책 목록을 조회한다")
    void getLikedBooksByUserId_Success() throws Exception {
        // given
        Long userId = 1L;
        
        BookResponse book1 = new BookResponse();
        book1.setIsbn("9788960777330");
        book1.setTitle("클린 코드");
        book1.setAuthor("로버트 C. 마틴");
        
        BookResponse book2 = new BookResponse();
        book2.setIsbn("9788966262281");
        book2.setTitle("이펙티브 자바");
        book2.setAuthor("조슈아 블로크");
        
        List<BookResponse> books = Arrays.asList(book1, book2);
        
        given(likeService.getLikedBooksByUserId(anyLong())).willReturn(books);

        // when & then
        mockMvc.perform(get("/api/likes/user")
                        .header("X-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].isbn").value("9788960777330"))
                .andExpect(jsonPath("$[0].title").value("클린 코드"))
                .andExpect(jsonPath("$[1].isbn").value("9788966262281"))
                .andExpect(jsonPath("$[1].title").value("이펙티브 자바"));
    }

    @Test
    @DisplayName("좋아요한 책이 없는 경우 204 No Content를 반환한다")
    void getLikedBooksByUserId_EmptyList_NoContent() throws Exception {
        // given
        Long userId = 1L;
        List<BookResponse> emptyBooks = Arrays.asList();
        
        given(likeService.getLikedBooksByUserId(anyLong())).willReturn(emptyBooks);

        // when & then
        mockMvc.perform(get("/api/likes/user")
                        .header("X-User-Id", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("특정 책에 좋아요했는지 확인한다 - 좋아요함")
    void checkLikeStatus_True() throws Exception {
        // given
        Long userId = 1L;
        String bookIsbn = "9788960777330";
        
        given(likeService.isLiked(anyLong(), anyString())).willReturn(true);

        // when & then
        mockMvc.perform(get("/api/likes/check")
                        .header("X-User-Id", userId)
                        .param("bookIsbn", bookIsbn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    @DisplayName("특정 책에 좋아요했는지 확인한다 - 좋아요하지 않음")
    void checkLikeStatus_False() throws Exception {
        // given
        Long userId = 1L;
        String bookIsbn = "9788960777330";
        
        given(likeService.isLiked(anyLong(), anyString())).willReturn(false);

        // when & then
        mockMvc.perform(get("/api/likes/check")
                        .header("X-User-Id", userId)
                        .param("bookIsbn", bookIsbn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    @DisplayName("bookIsbn 파라미터 없이 좋아요 상태 확인 시 오류가 발생한다")
    void checkLikeStatus_NoBookIsbnParam_Error() throws Exception {
        // given
        Long userId = 1L;

        // when & then
        mockMvc.perform(get("/api/likes/check")
                        .header("X-User-Id", userId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("User-Id 헤더 없이 좋아요 목록 조회 시 오류가 발생한다")
    void getLikedBooksByUserId_NoUserIdHeader_Error() throws Exception {
        // when & then
        mockMvc.perform(get("/api/likes/user"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("User-Id 헤더 없이 좋아요 상태 확인 시 오류가 발생한다")
    void checkLikeStatus_NoUserIdHeader_Error() throws Exception {
        // given
        String bookIsbn = "9788960777330";

        // when & then
        mockMvc.perform(get("/api/likes/check")
                        .param("bookIsbn", bookIsbn))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("잘못된 형식의 User-Id 헤더로 요청 시 오류가 발생한다")
    void addLike_InvalidUserIdHeader_Error() throws Exception {
        // given
        LikeRequest request = new LikeRequest();
        request.setBookIsbn("9788960777330");

        // when & then
        mockMvc.perform(post("/api/likes")
                        .header("X-User-Id", "invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("null bookIsbn으로 좋아요 추가 시 유효성 검증 오류가 발생한다")
    void addLike_NullBookIsbn_ValidationError() throws Exception {
        // given
        Long userId = 1L;
        LikeRequest request = new LikeRequest();
        request.setBookIsbn(null);

        // when & then
        mockMvc.perform(post("/api/likes")
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}