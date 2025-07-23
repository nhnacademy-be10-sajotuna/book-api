package com.sajotuna.books.book.service.impl;

import com.sajotuna.books.book.domain.Book;
import com.sajotuna.books.book.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookListServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookListServiceImpl bookListService;

    @Test
    @DisplayName("도서 목록 일괄 저장 성공")
    void saveAllBooks_ShouldSaveAllBooks() {
        // Given
        Book book1 = createTestBook("9788960777330", "테스트 도서1");
        Book book2 = createTestBook("9788960777331", "테스트 도서2");
        List<Book> books = List.of(book1, book2);

        // When
        bookListService.saveAllBooks(books);

        // Then
        verify(bookRepository, times(1)).saveAll(books);
    }

    @Test
    @DisplayName("빈 도서 목록 저장 - Repository 호출됨")
    void saveAllBooks_ShouldCallRepository_WhenEmptyList() {
        // Given
        List<Book> emptyBooks = List.of();

        // When
        bookListService.saveAllBooks(emptyBooks);

        // Then
        verify(bookRepository, times(1)).saveAll(emptyBooks);
    }

    private Book createTestBook(String isbn, String title) {
        return new Book(
                isbn,
                title,
                "테스트 저자",
                "테스트 출판사",
                LocalDate.now(),
                300,
                "http://test.com/cover.jpg",
                "테스트 설명",
                15000.0,
                13500.0,
                true,
                0
        );
    }
}