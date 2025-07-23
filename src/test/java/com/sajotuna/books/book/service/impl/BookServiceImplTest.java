package com.sajotuna.books.book.service.impl;

import com.sajotuna.books.book.OrderStockClient;
import com.sajotuna.books.book.controller.request.BookCreateRequest;
import com.sajotuna.books.book.controller.response.BookResponse;
import com.sajotuna.books.book.domain.Book;
import com.sajotuna.books.book.exception.BookNotFoundException;
import com.sajotuna.books.book.repository.BookRepository;
import com.sajotuna.books.category.service.CategoryService;
import com.sajotuna.books.like.repository.LikeRepository;
import com.sajotuna.books.search.BookSearchDocument;
import com.sajotuna.books.search.repository.BookSearchRepository;
import com.sajotuna.books.search.service.BookStatsService;
import com.sajotuna.books.tag.service.TagService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;
    
    @Mock
    private BookSearchRepository bookSearchRepository;
    
    @Mock
    private CategoryService categoryService;
    
    @Mock
    private TagService tagService;
    
    @Mock
    private LikeRepository likeRepository;
    
    @Mock
    private OrderStockClient orderStockClient;
    
    @Mock
    private EntityManager entityManager;
    
    @Mock
    private BookStatsService bookStatsService;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("모든 도서 조회 성공")
    void getAllBooks_ShouldReturnPageOfBooks() {
        // Given
        Book book = createTestBook();
        Page<Book> bookPage = new PageImpl<>(List.of(book));
        Pageable pageable = PageRequest.of(0, 10);
        
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        // When
        Page<BookResponse> result = bookService.getAllBooks(pageable);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getIsbn()).isEqualTo("9788960777330");
        verify(bookRepository).findAll(pageable);
    }

    @Test
    @DisplayName("ISBN으로 도서 조회 성공 - 조회수 증가")
    void getBookByIsbn_ShouldReturnBook_WhenBookExists() {
        // Given
        String isbn = "9788960777330";
        Book book = createTestBook();
        BookSearchDocument bookDoc = BookSearchDocument.from(book);
        
        when(bookRepository.findById(isbn)).thenReturn(Optional.of(book));
        when(bookSearchRepository.findById(isbn)).thenReturn(Optional.of(bookDoc));

        // When
        BookResponse result = bookService.getBookByIsbn(isbn);

        // Then
        assertThat(result.getIsbn()).isEqualTo(isbn);
        verify(bookStatsService).incrementViewCount(isbn);
        verify(bookRepository).findById(isbn);
        verify(bookSearchRepository).findById(isbn);
    }

    @Test
    @DisplayName("존재하지 않는 ISBN으로 조회 시 예외 발생")
    void getBookByIsbn_ShouldThrowException_WhenBookNotExists() {
        // Given
        String isbn = "non-existent-isbn";
        when(bookRepository.findById(isbn)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> bookService.getBookByIsbn(isbn))
                .isInstanceOf(BookNotFoundException.class);
        verify(bookStatsService, never()).incrementViewCount(anyString());
    }

    @Test
    @DisplayName("관리자용 도서 조회 성공 - 조회수 증가 없음")
    void getBookByIsbnByAdmin_ShouldReturnBook_WithoutIncrementingViews() {
        // Given
        String isbn = "9788960777330";
        Book book = createTestBook();
        
        when(bookRepository.findById(isbn)).thenReturn(Optional.of(book));

        // When
        BookResponse result = bookService.getBookByIsbnByAdmin(isbn);

        // Then
        assertThat(result.getIsbn()).isEqualTo(isbn);
        verify(bookStatsService, never()).incrementViewCount(anyString());
        verify(bookRepository).findById(isbn);
    }

    @Test
    @DisplayName("도서 생성 성공")
    void createBook_ShouldCreateBook_WhenValidRequest() {
        // Given
        BookCreateRequest request = createBookRequest();
        Book book = createTestBook();
        
        when(bookRepository.existsById(request.getIsbn())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // When
        BookResponse result = bookService.createBook(request);

        // Then
        assertThat(result.getIsbn()).isEqualTo(request.getIsbn());
        verify(bookRepository).existsById(request.getIsbn());
        verify(bookRepository).save(any(Book.class));
        verify(bookSearchRepository).save(any(BookSearchDocument.class));
    }

    @Test
    @DisplayName("중복 ISBN으로 도서 생성 시 예외 발생")
    void createBook_ShouldThrowException_WhenIsbnAlreadyExists() {
        // Given
        BookCreateRequest request = createBookRequest();
        when(bookRepository.existsById(request.getIsbn())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> bookService.createBook(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 ISBN입니다");
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("도서 수정 성공")
    void updateBook_ShouldUpdateBook_WhenBookExists() {
        // Given
        String isbn = "9788960777330";
        BookCreateRequest request = createBookRequest();
        Book book = createTestBook();
        
        when(bookRepository.findById(isbn)).thenReturn(Optional.of(book));

        // When
        BookResponse result = bookService.updateBook(isbn, request);

        // Then
        assertThat(result.getIsbn()).isEqualTo(isbn);
        verify(bookRepository).findById(isbn);
        verify(bookSearchRepository).save(any(BookSearchDocument.class));
        verify(entityManager).flush();
    }

    @Test
    @DisplayName("존재하지 않는 도서 수정 시 예외 발생")
    void updateBook_ShouldThrowException_WhenBookNotExists() {
        // Given
        String isbn = "non-existent-isbn";
        BookCreateRequest request = createBookRequest();
        when(bookRepository.findById(isbn)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> bookService.updateBook(isbn, request))
                .isInstanceOf(BookNotFoundException.class);
        verify(bookSearchRepository, never()).save(any(BookSearchDocument.class));
    }

    @Test
    @DisplayName("도서 삭제 성공")
    void deleteBook_ShouldDeleteBook_WhenBookExists() {
        // Given
        String isbn = "9788960777330";
        Book book = createTestBook();
        
        when(bookRepository.findById(isbn)).thenReturn(Optional.of(book));

        // When
        bookService.deleteBook(isbn);

        // Then
        verify(bookRepository).findById(isbn);
        verify(bookRepository).delete(book);
        verify(bookSearchRepository).deleteById(isbn);
    }

    @Test
    @DisplayName("좋아요 수 업데이트 성공")
    void updateBookLikes_ShouldUpdateLikes_WhenBookExists() {
        // Given
        String isbn = "9788960777330";
        Integer newLikes = 100;
        Book book = createTestBook();
        
        when(bookRepository.findById(isbn)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // When
        BookResponse result = bookService.updateBookLikes(isbn, newLikes);

        // Then
        assertThat(result.getLikes()).isEqualTo(newLikes);
        verify(bookRepository).findById(isbn);
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("좋아요 순으로 도서 조회 성공")
    void getBooksByLikesDesc_ShouldReturnBooksOrderedByLikes() {
        // Given
        Book book = createTestBook();
        Page<Book> bookPage = new PageImpl<>(List.of(book));
        Pageable pageable = PageRequest.of(0, 10);
        
        when(bookRepository.findAllByOrderByLikesDesc(pageable)).thenReturn(bookPage);

        // When
        Page<BookResponse> result = bookService.getBooksByLikesDesc(pageable);

        // Then
        assertThat(result).hasSize(1);
        verify(bookRepository).findAllByOrderByLikesDesc(pageable);
    }

    @Test
    @DisplayName("재고 업데이트 성공")
    void updateBookStock_ShouldUpdateStock_WhenBookExists() {
        // Given
        String isbn = "9788960777330";
        Integer stock = 50;
        
        when(bookRepository.existsById(isbn)).thenReturn(true);

        // When
        bookService.updateBookStock(isbn, stock);

        // Then
        verify(bookRepository).existsById(isbn);
        verify(orderStockClient).updateStock(any());
    }

    @Test
    @DisplayName("존재하지 않는 도서의 재고 업데이트 시 예외 발생")
    void updateBookStock_ShouldThrowException_WhenBookNotExists() {
        // Given
        String isbn = "non-existent-isbn";
        Integer stock = 50;
        
        when(bookRepository.existsById(isbn)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> bookService.updateBookStock(isbn, stock))
                .isInstanceOf(BookNotFoundException.class);
        verify(orderStockClient, never()).updateStock(any());
    }

    @Test
    @DisplayName("리뷰 정보 업데이트 성공")
    void updateReviewInfo_ShouldUpdateReviewInfo_WhenBookExists() {
        // Given
        String isbn = "9788960777330";
        double rating = 4.5;
        Book book = createTestBook();
        BookSearchDocument bookDoc = BookSearchDocument.from(book);
        bookDoc.setAverageRating(4.0);
        bookDoc.setReviewCount(10);
        
        when(bookRepository.findById(isbn)).thenReturn(Optional.of(book));
        when(bookSearchRepository.findById(isbn)).thenReturn(Optional.of(bookDoc));

        // When
        bookService.updateReviewInfo(isbn, rating);

        // Then
        verify(bookRepository).findById(isbn);
        verify(bookSearchRepository).findById(isbn);
        verify(bookStatsService).updateReviewStats(eq(isbn), anyDouble(), eq(11));
    }

    private Book createTestBook() {
        return new Book(
                "9788960777330",
                "테스트 도서",
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

    private BookCreateRequest createBookRequest() {
        BookCreateRequest request = new BookCreateRequest();
        request.setIsbn("9788960777330");
        request.setTitle("테스트 도서");
        request.setAuthor("테스트 저자");
        request.setPublisher("테스트 출판사");
        request.setPublicationDate(LocalDate.now());
        request.setPageCount(300);
        request.setImageUrl("http://test.com/cover.jpg");
        request.setDescription("테스트 설명");
        request.setOriginalPrice(15000.0);
        request.setSellingPrice(13500.0);
        request.setGiftWrappingAvailable(true);
        request.setLikes(0);
        return request;
    }
}