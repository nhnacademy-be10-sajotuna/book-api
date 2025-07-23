package com.sajotuna.books.book.controller;

import com.sajotuna.books.book.controller.response.AladinBookResponse;
import com.sajotuna.books.book.service.AladinBookImportService;
import com.sajotuna.books.book.service.AladinFetchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AladinBookImportController.class)
@ActiveProfiles("test")
class AladinBookImportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AladinFetchService aladinFetchService;

    @MockitoBean
    private AladinBookImportService aladinBookImportService;

    @Test
    @DisplayName("알라딘 API에서 책을 가져와서 DB에 저장한다")
    void importBooks_Success() throws Exception {
        // given
        String keyword = "자바";
        int totalPages = 2;
        
        AladinBookResponse book1 = new AladinBookResponse();
        book1.setTitle("이펙티브 자바");
        book1.setAuthor("조슈아 블로크");
        book1.setIsbn13("9788966262281");
        book1.setPriceSales(32400);
        book1.setPriceStandard(36000);
        book1.setPublisher("인사이트");
        book1.setCategoryName("국내도서>컴퓨터/모바일>프로그래밍 언어>자바");

        AladinBookResponse book2 = new AladinBookResponse();
        book2.setTitle("자바의 정석");
        book2.setAuthor("남궁성");
        book2.setIsbn13("9788994492032");
        book2.setPriceSales(27000);
        book2.setPriceStandard(30000);
        book2.setPublisher("도우출판");
        book2.setCategoryName("국내도서>컴퓨터/모바일>프로그래밍 언어>자바");

        List<AladinBookResponse> books = Arrays.asList(book1, book2);
        
        given(aladinFetchService.fetchBooks(anyString(), anyInt())).willReturn(books);
        doNothing().when(aladinBookImportService).importBooks(anyList());

        // when & then
        mockMvc.perform(post("/api/admin/search/import")
                        .param("keyword", keyword)
                        .param("totalPages", String.valueOf(totalPages)))
                .andExpect(status().isOk());

        // verify
        verify(aladinFetchService).fetchBooks(keyword, totalPages);
        verify(aladinBookImportService).importBooks(books);
    }

    @Test
    @DisplayName("빈 결과로 책을 가져와도 정상 처리된다")
    void importBooks_EmptyResult_Success() throws Exception {
        // given
        String keyword = "존재하지않는책";
        int totalPages = 1;
        List<AladinBookResponse> emptyBooks = Arrays.asList();
        
        given(aladinFetchService.fetchBooks(anyString(), anyInt())).willReturn(emptyBooks);
        doNothing().when(aladinBookImportService).importBooks(anyList());

        // when & then
        mockMvc.perform(post("/api/admin/search/import")
                        .param("keyword", keyword)
                        .param("totalPages", String.valueOf(totalPages)))
                .andExpect(status().isOk());

        // verify
        verify(aladinFetchService).fetchBooks(keyword, totalPages);
        verify(aladinBookImportService).importBooks(emptyBooks);
    }

    @Test
    @DisplayName("여러 페이지를 가져와서 책을 저장한다")
    void importBooks_MultiplePages_Success() throws Exception {
        // given
        String keyword = "스프링";
        int totalPages = 5;
        
        AladinBookResponse book = new AladinBookResponse();
        book.setTitle("스프링 부트와 AWS로 혼자 구현하는 웹 서비스");
        book.setAuthor("이동욱");
        book.setIsbn13("9788965402602");
        book.setPriceSales(23400);
        book.setPriceStandard(26000);
        book.setPublisher("프리렉");
        book.setCategoryName("국내도서>컴퓨터/모바일>웹프로그래밍");

        List<AladinBookResponse> books = Arrays.asList(book);
        
        given(aladinFetchService.fetchBooks(anyString(), anyInt())).willReturn(books);
        doNothing().when(aladinBookImportService).importBooks(anyList());

        // when & then
        mockMvc.perform(post("/api/admin/search/import")
                        .param("keyword", keyword)
                        .param("totalPages", String.valueOf(totalPages)))
                .andExpect(status().isOk());

        // verify
        verify(aladinFetchService).fetchBooks(keyword, totalPages);
        verify(aladinBookImportService).importBooks(books);
    }

    @Test
    @DisplayName("페이지 수가 0이면 정상 처리된다")
    void importBooks_ZeroPages_Success() throws Exception {
        // given
        String keyword = "테스트";
        int totalPages = 0;
        List<AladinBookResponse> emptyBooks = Arrays.asList();
        
        given(aladinFetchService.fetchBooks(anyString(), anyInt())).willReturn(emptyBooks);
        doNothing().when(aladinBookImportService).importBooks(anyList());

        // when & then
        mockMvc.perform(post("/api/admin/search/import")
                        .param("keyword", keyword)
                        .param("totalPages", String.valueOf(totalPages)))
                .andExpect(status().isOk());

        // verify
        verify(aladinFetchService).fetchBooks(keyword, totalPages);
        verify(aladinBookImportService).importBooks(emptyBooks);
    }
}