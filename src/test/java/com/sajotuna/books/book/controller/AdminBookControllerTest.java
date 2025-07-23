package com.sajotuna.books.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.books.book.controller.request.BookCreateRequest;
import com.sajotuna.books.book.controller.response.BookResponse;
import com.sajotuna.books.book.service.BookService;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminBookController.class)
@ActiveProfiles("test")
class AdminBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("모든 책 목록을 페이지네이션으로 조회한다")
    void getAllBooks_Success() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        BookResponse book1 = new BookResponse();
        book1.setIsbn("9788960777330");
        book1.setTitle("클린 코드");
        book1.setAuthor("로버트 C. 마틴");
        book1.setPublisher("인사이트");
        book1.setSellingPrice(33000.0);
        book1.setLikes(100);

        BookResponse book2 = new BookResponse();
        book2.setIsbn("9788966262281");
        book2.setTitle("이펙티브 자바");
        book2.setAuthor("조슈아 블로크");
        book2.setPublisher("인사이트");
        book2.setSellingPrice(36000.0);
        book2.setLikes(120);

        List<BookResponse> books = Arrays.asList(book1, book2);
        Page<BookResponse> pagedBooks = new PageImpl<>(books, pageable, books.size());
        
        given(bookService.getAllBooks(any(Pageable.class))).willReturn(pagedBooks);

        // when & then
        mockMvc.perform(get("/api/admin/books")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].isbn").value("9788960777330"))
                .andExpect(jsonPath("$.content[1].isbn").value("9788966262281"))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0));
    }

    @Test
    @DisplayName("관리자가 ISBN으로 책 상세 정보를 조회한다")
    void getBookByIsbn_Success() throws Exception {
        // given
        String isbn = "9788960777330";
        BookResponse bookResponse = new BookResponse();
        bookResponse.setIsbn(isbn);
        bookResponse.setTitle("클린 코드");
        bookResponse.setAuthor("로버트 C. 마틴");
        bookResponse.setPublisher("인사이트");
        bookResponse.setSellingPrice(33000.0);
        bookResponse.setLikes(100);

        given(bookService.getBookByIsbnByAdmin(anyString())).willReturn(bookResponse);

        // when & then
        mockMvc.perform(get("/api/admin/books/{isbn}", isbn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value(isbn))
                .andExpect(jsonPath("$.title").value("클린 코드"))
                .andExpect(jsonPath("$.author").value("로버트 C. 마틴"));
    }

    @Test
    @DisplayName("새로운 책을 생성한다")
    void createBook_Success() throws Exception {
        // given
        BookCreateRequest request = new BookCreateRequest();
        request.setIsbn("9788960777330");
        request.setTitle("클린 코드");
        request.setAuthor("로버트 C. 마틴");
        request.setPublisher("인사이트");
        request.setPublicationDate(LocalDate.of(2013, 12, 24));
        request.setPageCount(584);
        request.setOriginalPrice(35000.0);
        request.setSellingPrice(33000.0);
        request.setGiftWrappingAvailable(true);
        request.setLikes(0);

        BookResponse bookResponse = new BookResponse();
        bookResponse.setIsbn("9788960777330");
        bookResponse.setTitle("클린 코드");
        bookResponse.setAuthor("로버트 C. 마틴");
        bookResponse.setPublisher("인사이트");
        bookResponse.setSellingPrice(33000.0);
        bookResponse.setLikes(0);

        given(bookService.createBook(any(BookCreateRequest.class))).willReturn(bookResponse);

        // when & then
        mockMvc.perform(post("/api/admin/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isbn").value("9788960777330"))
                .andExpect(jsonPath("$.title").value("클린 코드"))
                .andExpect(jsonPath("$.author").value("로버트 C. 마틴"));
    }

    @Test
    @DisplayName("책 정보를 수정한다")
    void updateBook_Success() throws Exception {
        // given
        String isbn = "9788960777330";
        BookCreateRequest request = new BookCreateRequest();
        request.setIsbn(isbn);
        request.setTitle("클린 코드 (수정판)");
        request.setAuthor("로버트 C. 마틴");
        request.setPublisher("인사이트");
        request.setPublicationDate(LocalDate.of(2013, 12, 24));
        request.setPageCount(584);
        request.setOriginalPrice(35000.0);
        request.setSellingPrice(33000.0);

        BookResponse bookResponse = new BookResponse();
        bookResponse.setIsbn(isbn);
        bookResponse.setTitle("클린 코드 (수정판)");
        bookResponse.setAuthor("로버트 C. 마틴");
        bookResponse.setPublisher("인사이트");
        bookResponse.setSellingPrice(33000.0);

        given(bookService.updateBook(anyString(), any(BookCreateRequest.class))).willReturn(bookResponse);

        // when & then
        mockMvc.perform(put("/api/admin/books/{isbn}", isbn)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value(isbn))
                .andExpect(jsonPath("$.title").value("클린 코드 (수정판)"));
    }

    @Test
    @DisplayName("책을 삭제한다")
    void deleteBook_Success() throws Exception {
        // given
        String isbn = "9788960777330";
        doNothing().when(bookService).deleteBook(anyString());

        // when & then
        mockMvc.perform(delete("/api/admin/books/{isbn}", isbn))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("책 재고를 수정한다")
    void updateBookStock_Success() throws Exception {
        // given
        String isbn = "9788960777330";
        Integer stock = 100;
        doNothing().when(bookService).updateBookStock(anyString(), anyInt());

        // when & then
        mockMvc.perform(patch("/api/admin/books/{isbn}/stock", isbn)
                        .param("stock", stock.toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("책 좋아요 수를 수정한다")
    void updateBookLikes_Success() throws Exception {
        // given
        String isbn = "9788960777330";
        Integer likes = 150;
        
        BookResponse bookResponse = new BookResponse();
        bookResponse.setIsbn(isbn);
        bookResponse.setTitle("클린 코드");
        bookResponse.setLikes(likes);

        given(bookService.updateBookLikes(anyString(), anyInt())).willReturn(bookResponse);

        // when & then
        mockMvc.perform(patch("/api/admin/books/{isbn}/likes", isbn)
                        .param("likes", likes.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value(isbn))
                .andExpect(jsonPath("$.likes").value(likes));
    }

    @Test
    @DisplayName("잘못된 ISBN으로 책 생성 시 유효성 검증 오류가 발생한다")
    void createBook_InvalidIsbn_ValidationError() throws Exception {
        // given
        BookCreateRequest request = new BookCreateRequest();
        request.setIsbn(""); // 빈 ISBN
        request.setTitle("클린 코드");
        request.setAuthor("로버트 C. 마틴");
        request.setPublisher("인사이트");
        request.setPublicationDate(LocalDate.of(2013, 12, 24));
        request.setOriginalPrice(35000.0);
        request.setSellingPrice(33000.0);

        // when & then
        mockMvc.perform(post("/api/admin/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("음수 재고로 수정 시 유효성 검증 오류가 발생한다")
    void updateBookStock_NegativeStock_ValidationError() throws Exception {
        // given
        String isbn = "9788960777330";
        Integer stock = -1; // 음수 재고

        // when & then
        mockMvc.perform(patch("/api/admin/books/{isbn}/stock", isbn)
                        .param("stock", stock.toString()))
                .andExpect(status().isInternalServerError());
    }
}