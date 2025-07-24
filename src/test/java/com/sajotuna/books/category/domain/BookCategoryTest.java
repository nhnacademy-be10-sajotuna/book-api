package com.sajotuna.books.category.domain;

import com.sajotuna.books.book.domain.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookCategoryTest {

    private Book book;
    private Category category;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setIsbn("9788960777330");
        book.setTitle("클린 코드");

        category = new Category();
        category.setId(1L);
        category.setName("프로그래밍");
    }

    @Test
    @DisplayName("기본 생성자로 BookCategory 객체를 생성한다")
    void createBookCategory_WithNoArgsConstructor_Success() {
        // when
        BookCategory bookCategory = new BookCategory();

        // then
        assertThat(bookCategory.getId()).isNull();
        assertThat(bookCategory.getBook()).isNull();
        assertThat(bookCategory.getCategory()).isNull();
    }

    @Test
    @DisplayName("전체 인자 생성자로 BookCategory 객체를 생성한다")
    void createBookCategory_WithAllArgsConstructor_Success() {
        // when
        BookCategory bookCategory = new BookCategory(1L, book, category);

        // then
        assertThat(bookCategory.getId()).isEqualTo(1L);
        assertThat(bookCategory.getBook()).isEqualTo(book);
        assertThat(bookCategory.getCategory()).isEqualTo(category);
    }

    @Test
    @DisplayName("BookCategory의 ID를 설정하고 조회할 수 있다")
    void setAndGetId_Success() {
        // given
        BookCategory bookCategory = new BookCategory();
        Long id = 1L;

        // when
        bookCategory.setId(id);

        // then
        assertThat(bookCategory.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("BookCategory의 Book을 설정하고 조회할 수 있다")
    void setAndGetBook_Success() {
        // given
        BookCategory bookCategory = new BookCategory();

        // when
        bookCategory.setBook(book);

        // then
        assertThat(bookCategory.getBook()).isEqualTo(book);
        assertThat(bookCategory.getBook().getIsbn()).isEqualTo("9788960777330");
        assertThat(bookCategory.getBook().getTitle()).isEqualTo("클린 코드");
    }

    @Test
    @DisplayName("BookCategory의 Category를 설정하고 조회할 수 있다")
    void setAndGetCategory_Success() {
        // given
        BookCategory bookCategory = new BookCategory();

        // when
        bookCategory.setCategory(category);

        // then
        assertThat(bookCategory.getCategory()).isEqualTo(category);
        assertThat(bookCategory.getCategory().getId()).isEqualTo(1L);
        assertThat(bookCategory.getCategory().getName()).isEqualTo("프로그래밍");
    }

    @Test
    @DisplayName("null Book으로 BookCategory를 생성할 수 있다")
    void createBookCategory_WithNullBook_Success() {
        // when
        BookCategory bookCategory = new BookCategory(1L, null, category);

        // then
        assertThat(bookCategory.getId()).isEqualTo(1L);
        assertThat(bookCategory.getBook()).isNull();
        assertThat(bookCategory.getCategory()).isEqualTo(category);
    }

    @Test
    @DisplayName("null Category로 BookCategory를 생성할 수 있다")
    void createBookCategory_WithNullCategory_Success() {
        // when
        BookCategory bookCategory = new BookCategory(1L, book, null);

        // then
        assertThat(bookCategory.getId()).isEqualTo(1L);
        assertThat(bookCategory.getBook()).isEqualTo(book);
        assertThat(bookCategory.getCategory()).isNull();
    }

    @Test
    @DisplayName("null ID로 BookCategory를 생성할 수 있다")
    void createBookCategory_WithNullId_Success() {
        // when
        BookCategory bookCategory = new BookCategory(null, book, category);

        // then
        assertThat(bookCategory.getId()).isNull();
        assertThat(bookCategory.getBook()).isEqualTo(book);
        assertThat(bookCategory.getCategory()).isEqualTo(category);
    }

    @Test
    @DisplayName("0L ID로 BookCategory를 생성할 수 있다")
    void createBookCategory_WithZeroId_Success() {
        // when
        BookCategory bookCategory = new BookCategory(0L, book, category);

        // then
        assertThat(bookCategory.getId()).isEqualTo(0L);
        assertThat(bookCategory.getBook()).isEqualTo(book);
        assertThat(bookCategory.getCategory()).isEqualTo(category);
    }

    @Test
    @DisplayName("음수 ID로 BookCategory를 생성할 수 있다")
    void createBookCategory_WithNegativeId_Success() {
        // when
        BookCategory bookCategory = new BookCategory(-1L, book, category);

        // then
        assertThat(bookCategory.getId()).isEqualTo(-1L);
        assertThat(bookCategory.getBook()).isEqualTo(book);
        assertThat(bookCategory.getCategory()).isEqualTo(category);
    }

    @Test
    @DisplayName("매우 큰 ID로 BookCategory를 생성할 수 있다")
    void createBookCategory_WithLargeId_Success() {
        // when
        BookCategory bookCategory = new BookCategory(Long.MAX_VALUE, book, category);

        // then
        assertThat(bookCategory.getId()).isEqualTo(Long.MAX_VALUE);
        assertThat(bookCategory.getBook()).isEqualTo(book);
        assertThat(bookCategory.getCategory()).isEqualTo(category);
    }

    @Test
    @DisplayName("Book 정보가 변경되어도 BookCategory는 참조를 유지한다")
    void bookCategory_BookReference_MaintainedAfterBookChange() {
        // given
        BookCategory bookCategory = new BookCategory(1L, book, category);
        String originalTitle = book.getTitle();

        // when
        book.setTitle("변경된 제목");

        // then
        assertThat(bookCategory.getBook()).isEqualTo(book);
        assertThat(bookCategory.getBook().getTitle()).isEqualTo("변경된 제목");
        assertThat(bookCategory.getBook().getTitle()).isNotEqualTo(originalTitle);
    }

    @Test
    @DisplayName("Category 정보가 변경되어도 BookCategory는 참조를 유지한다")
    void bookCategory_CategoryReference_MaintainedAfterCategoryChange() {
        // given
        BookCategory bookCategory = new BookCategory(1L, book, category);
        String originalName = category.getName();

        // when
        category.setName("변경된 카테고리");

        // then
        assertThat(bookCategory.getCategory()).isEqualTo(category);
        assertThat(bookCategory.getCategory().getName()).isEqualTo("변경된 카테고리");
        assertThat(bookCategory.getCategory().getName()).isNotEqualTo(originalName);
    }

    @Test
    @DisplayName("다른 Book과 Category로 BookCategory를 생성할 수 있다")
    void createBookCategory_WithDifferentBookAndCategory_Success() {
        // given
        Book anotherBook = new Book();
        anotherBook.setIsbn("9788966262281");
        anotherBook.setTitle("이펙티브 자바");

        Category anotherCategory = new Category();
        anotherCategory.setId(2L);
        anotherCategory.setName("자바");

        // when
        BookCategory bookCategory = new BookCategory(2L, anotherBook, anotherCategory);

        // then
        assertThat(bookCategory.getId()).isEqualTo(2L);
        assertThat(bookCategory.getBook().getIsbn()).isEqualTo("9788966262281");
        assertThat(bookCategory.getBook().getTitle()).isEqualTo("이펙티브 자바");
        assertThat(bookCategory.getCategory().getId()).isEqualTo(2L);
        assertThat(bookCategory.getCategory().getName()).isEqualTo("자바");
    }
}