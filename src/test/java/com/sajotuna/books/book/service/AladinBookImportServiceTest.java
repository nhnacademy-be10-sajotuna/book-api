package com.sajotuna.books.book.service;

import com.sajotuna.books.book.controller.response.AladinBookResponse;
import com.sajotuna.books.book.domain.Book;
import com.sajotuna.books.book.repository.BookRepository;
import com.sajotuna.books.category.domain.Category;
import com.sajotuna.books.category.service.CategoryService;
import com.sajotuna.books.search.repository.BookSearchRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AladinBookImportServiceTest {

    @Mock
    private BookListService bookListService;
    
    @Mock
    private BookRepository bookRepository;
    
    @Mock
    private CategoryService categoryService;
    
    @Mock
    private BookSearchRepository bookSearchRepository;
    
    @Mock
    private AladinStockService aladinStockService;

    @InjectMocks
    private AladinBookImportService aladinBookImportService;

    @Test
    @DisplayName("도서 가져오기 - RDB와 ES 모두 저장됨")
    void importBooks_ShouldSaveToBothRdbAndEs() {
        // Given
        AladinBookResponse response = createMockResponse();
        Category category = new Category();
        
        when(categoryService.findOrCreateCategories(anyList())).thenReturn(List.of(category));
        when(bookRepository.existsById(any())).thenReturn(false);

        // When
        aladinBookImportService.importBooks(List.of(response));

        // Then
        verify(bookListService, times(1)).saveAllBooks(any()); // RDB 저장
        verify(bookSearchRepository, times(1)).saveAll(any()); // ES 저장
        verify(aladinStockService, times(1)).syncStockWithOrderApi(any());
    }

    @Test
    @DisplayName("이미 존재하는 도서는 저장하지 않음")
    void importBooks_ShouldNotSaveExistingBooks() {
        // Given
        AladinBookResponse response = createMockResponse();
        Category category = new Category();
        
        when(categoryService.findOrCreateCategories(anyList())).thenReturn(List.of(category));
        when(bookRepository.existsById(any())).thenReturn(true); // 이미 존재

        // When
        aladinBookImportService.importBooks(List.of(response));

        // Then
        verify(bookListService).saveAllBooks(argThat(books -> books.isEmpty()));
        verify(aladinStockService).syncStockWithOrderApi(argThat(books -> books.isEmpty()));
    }

    @Test
    @DisplayName("카테고리가 없는 경우 도서를 저장하지 않음")
    void importBooks_ShouldNotSaveWhenNoCategoriesFound() {
        // Given
        AladinBookResponse response = createMockResponse();
        
        when(categoryService.findOrCreateCategories(anyList())).thenReturn(List.of()); // 빈 카테고리

        // When
        aladinBookImportService.importBooks(List.of(response));

        // Then
        verify(bookListService).saveAllBooks(argThat(books -> books.isEmpty()));
        verify(aladinStockService).syncStockWithOrderApi(argThat(books -> books.isEmpty()));
    }

    private AladinBookResponse createMockResponse() {
        AladinBookResponse response = new AladinBookResponse();
        response.setIsbn("9788960777330");
        response.setTitle("테스트 도서");
        response.setAuthor("테스트 저자");
        response.setPublisher("테스트 출판사");
        response.setPubDate("2023-01-01");
        response.setDescription("테스트 설명");
        response.setCover("http://test.com/cover.jpg");
        response.setCategoryName("IT>프로그래밍");
        response.setPriceStandard(15000);
        response.setPriceSales(13500);
        return response;
    }
}