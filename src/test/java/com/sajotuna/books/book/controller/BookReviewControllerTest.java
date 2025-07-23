package com.sajotuna.books.book.controller;

import com.sajotuna.books.book.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookReviewController.class)
@ActiveProfiles("test")
class BookReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;


    @Test
    @DisplayName("책에 리뷰를 추가한다")
    void addReview_Success() throws Exception {
        // given
        String isbn = "9788960777330";
        double rating = 4.5;
        
        doNothing().when(bookService).updateReviewInfo(anyString(), anyDouble());

        // when & then
        mockMvc.perform(post("/api/books/{isbn}/review", isbn)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(rating)))
                .andExpect(status().isOk());

        // verify
        verify(bookService).updateReviewInfo(isbn, rating);
    }

    @Test
    @DisplayName("최고 평점 5.0으로 리뷰를 추가한다")
    void addReview_MaxRating_Success() throws Exception {
        // given
        String isbn = "9788960777330";
        double rating = 5.0;
        
        doNothing().when(bookService).updateReviewInfo(anyString(), anyDouble());

        // when & then
        mockMvc.perform(post("/api/books/{isbn}/review", isbn)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(rating)))
                .andExpect(status().isOk());

        // verify
        verify(bookService).updateReviewInfo(isbn, rating);
    }

    @Test
    @DisplayName("최저 평점 1.0으로 리뷰를 추가한다")
    void addReview_MinRating_Success() throws Exception {
        // given
        String isbn = "9788960777330";
        double rating = 1.0;
        
        doNothing().when(bookService).updateReviewInfo(anyString(), anyDouble());

        // when & then
        mockMvc.perform(post("/api/books/{isbn}/review", isbn)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(rating)))
                .andExpect(status().isOk());

        // verify
        verify(bookService).updateReviewInfo(isbn, rating);
    }

    @Test
    @DisplayName("소수점이 있는 평점으로 리뷰를 추가한다")
    void addReview_DecimalRating_Success() throws Exception {
        // given
        String isbn = "9788966262281";
        double rating = 3.7;
        
        doNothing().when(bookService).updateReviewInfo(anyString(), anyDouble());

        // when & then
        mockMvc.perform(post("/api/books/{isbn}/review", isbn)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(rating)))
                .andExpect(status().isOk());

        // verify
        verify(bookService).updateReviewInfo(isbn, rating);
    }

    @Test
    @DisplayName("0 평점으로 리뷰를 추가한다")
    void addReview_ZeroRating_Success() throws Exception {
        // given
        String isbn = "9788960777330";
        double rating = 0.0;
        
        doNothing().when(bookService).updateReviewInfo(anyString(), anyDouble());

        // when & then
        mockMvc.perform(post("/api/books/{isbn}/review", isbn)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(rating)))
                .andExpect(status().isOk());

        // verify
        verify(bookService).updateReviewInfo(isbn, rating);
    }

    @Test
    @DisplayName("음수 평점으로 리뷰를 추가한다")
    void addReview_NegativeRating_Success() throws Exception {
        // given
        String isbn = "9788960777330";
        double rating = -1.0;
        
        doNothing().when(bookService).updateReviewInfo(anyString(), anyDouble());

        // when & then
        mockMvc.perform(post("/api/books/{isbn}/review", isbn)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(rating)))
                .andExpect(status().isOk());

        // verify
        verify(bookService).updateReviewInfo(isbn, rating);
    }
}