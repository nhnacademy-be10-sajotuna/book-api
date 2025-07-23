package com.sajotuna.books.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.books.book.controller.request.BookBatchRequest;
import com.sajotuna.books.book.controller.response.BookResponse;
import com.sajotuna.books.book.controller.response.BookSummaryResponse;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@ActiveProfiles("test")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("ISBN으로 책 상세 정보를 조회한다")
    void getBookByIsbn_Success() throws Exception {
        // given
        String isbn = "9788960777330";
        BookResponse bookResponse = new BookResponse();
        bookResponse.setIsbn(isbn);
        bookResponse.setTitle("클린 코드");
        bookResponse.setAuthor("로버트 C. 마틴");
        bookResponse.setPublisher("인사이트");
        bookResponse.setSellingPrice(33000.0);
        bookResponse.setDescription("애자일 소프트웨어 장인 정신");
        bookResponse.setImageUrl("http://example.com/cover.jpg");
        bookResponse.setLikes(100);

        given(bookService.getBookByIsbn(anyString())).willReturn(bookResponse);

        // when & then
        mockMvc.perform(get("/api/books/{isbn}", isbn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value(isbn))
                .andExpect(jsonPath("$.title").value("클린 코드"))
                .andExpect(jsonPath("$.author").value("로버트 C. 마틴"))
                .andExpect(jsonPath("$.publisher").value("인사이트"))
                .andExpect(jsonPath("$.sellingPrice").value(33000.0))
                .andExpect(jsonPath("$.likes").value(100));
    }

    @Test
    @DisplayName("ISBN 목록으로 책들의 요약 정보를 조회한다")
    void getBooksByIsbns_Success() throws Exception {
        // given
        BookBatchRequest request = new BookBatchRequest(Arrays.asList("9788960777330", "9788966262281"));

        List<BookSummaryResponse> bookSummaries = Arrays.asList(
                BookSummaryResponse.builder()
                        .isbn("9788960777330")
                        .title("클린 코드")
                        .imageUrl("http://example.com/cover1.jpg")
                        .sellingPrice(33000.0)
                        .build(),
                BookSummaryResponse.builder()
                        .isbn("9788966262281")
                        .title("이펙티브 자바")
                        .imageUrl("http://example.com/cover2.jpg")
                        .sellingPrice(36000.0)
                        .build()
        );

        given(bookService.getBooksByIsbns(any(BookBatchRequest.class))).willReturn(bookSummaries);

        // when & then
        mockMvc.perform(post("/api/books/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].isbn").value("9788960777330"))
                .andExpect(jsonPath("$[0].title").value("클린 코드"))
                .andExpect(jsonPath("$[1].isbn").value("9788966262281"))
                .andExpect(jsonPath("$[1].title").value("이펙티브 자바"));
    }

    @Test
    @DisplayName("좋아요 많은 순서로 책 목록을 조회한다")
    void getBooksByLikesDesc_Success() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        BookResponse book1 = new BookResponse();
        book1.setIsbn("9788960777330");
        book1.setTitle("클린 코드");
        book1.setAuthor("로버트 C. 마틴");
        book1.setPublisher("인사이트");
        book1.setSellingPrice(33000.0);
        book1.setImageUrl("http://example.com/cover1.jpg");
        book1.setLikes(150);
        
        BookResponse book2 = new BookResponse();
        book2.setIsbn("9788966262281");
        book2.setTitle("이펙티브 자바");
        book2.setAuthor("조슈아 블로크");
        book2.setPublisher("인사이트");
        book2.setSellingPrice(36000.0);
        book2.setImageUrl("http://example.com/cover2.jpg");
        book2.setLikes(120);
        
        List<BookResponse> books = Arrays.asList(book1, book2);
        
        Page<BookResponse> pagedBooks = new PageImpl<>(books, pageable, books.size());
        given(bookService.getBooksByLikesDesc(any(Pageable.class))).willReturn(pagedBooks);

        // when & then
        mockMvc.perform(get("/api/books/likes")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].likes").value(150))
                .andExpect(jsonPath("$.content[1].likes").value(120))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(10));
    }

    @Test
    @DisplayName("빈 ISBN 목록으로 배치 요청 시 빈 배열을 반환한다")
    void getBooksByIsbns_EmptyList() throws Exception {
        // given
        BookBatchRequest request = new BookBatchRequest(Arrays.asList());

        given(bookService.getBooksByIsbns(any(BookBatchRequest.class))).willReturn(Arrays.asList());

        // when & then
        mockMvc.perform(post("/api/books/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}