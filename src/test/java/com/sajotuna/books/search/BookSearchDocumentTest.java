package com.sajotuna.books.search;

import com.sajotuna.books.book.domain.Book;
import com.sajotuna.books.category.domain.BookCategory;
import com.sajotuna.books.category.domain.Category;
import com.sajotuna.books.tag.domain.BookTag;
import com.sajotuna.books.tag.domain.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BookSearchDocumentTest {

    private Book book;
    private Category rootCategory;
    private Category childCategory;

    @BeforeEach
    void setUp() {
        // 카테고리 설정
        rootCategory = new Category();
        rootCategory.setId(1L);
        rootCategory.setName("도서");
        rootCategory.setParentCategory(null);

        childCategory = new Category();
        childCategory.setId(2L);
        childCategory.setName("컴퓨터/IT");
        childCategory.setParentCategory(rootCategory);

        // 책 설정
        book = new Book();
        book.setIsbn("9788960777330");
        book.setTitle("클린 코드");
        book.setDescription("애자일 소프트웨어 장인 정신");
        book.setAuthor("로버트 C. 마틴");
        book.setPublicationDate(LocalDate.of(2013, 12, 24));
        book.setSellingPrice(31500.0);
        book.setOriginalPrice(35000.0);
        book.setImageUrl("http://image.aladin.co.kr/product/1953/63/cover/8960777331_1.jpg");

        // BookCategory 설정
        BookCategory bookCategory = new BookCategory();
        bookCategory.setId(1L);
        bookCategory.setBook(book);
        bookCategory.setCategory(childCategory);

        Set<BookCategory> bookCategories = new HashSet<>();
        bookCategories.add(bookCategory);
        book.setBookCategories(bookCategories);

        // BookTag 설정
        Tag tag = new Tag("프로그래밍");
        tag.setId(1L);
        
        BookTag bookTag = new BookTag(tag, book);

        Set<BookTag> bookTags = new HashSet<>();
        bookTags.add(bookTag);
        book.setBookTags(bookTags);
    }

    @Test
    @DisplayName("기본 생성자로 BookSearchDocument 객체를 생성한다")
    void createBookSearchDocument_WithNoArgsConstructor_Success() {
        // when
        BookSearchDocument document = new BookSearchDocument();

        // then
        assertThat(document.getId()).isNull();
        assertThat(document.getIsbn()).isNull();
        assertThat(document.getTitle()).isNull();
        assertThat(document.getAuthor()).isNull();
        assertThat(document.getTags()).isNull();
        assertThat(document.getCategoryIds()).isNull();
    }

    @Test
    @DisplayName("전체 인자 생성자로 BookSearchDocument 객체를 생성한다")
    void createBookSearchDocument_WithAllArgsConstructor_Success() {
        // given
        Set<String> tags = Set.of("프로그래밍", "자바");
        Set<Long> categoryIds = Set.of(1L, 2L);

        // when
        BookSearchDocument document = new BookSearchDocument(
                "9788960777330",
                "9788960777330",
                "클린 코드",
                "클린 코드",
                "클린 코드",
                "애자일 소프트웨어 장인 정신",
                "로버트 C. 마틴",
                tags,
                LocalDate.of(2013, 12, 24),
                31500.0,
                35000.0,
                4.5,
                100,
                1000,
                50,
                "http://image.aladin.co.kr/product/1953/63/cover/8960777331_1.jpg",
                85.5,
                categoryIds
        );

        // then
        assertThat(document.getId()).isEqualTo("9788960777330");
        assertThat(document.getIsbn()).isEqualTo("9788960777330");
        assertThat(document.getTitle()).isEqualTo("클린 코드");
        assertThat(document.getAuthor()).isEqualTo("로버트 C. 마틴");
        assertThat(document.getTags()).containsExactlyInAnyOrder("프로그래밍", "자바");
        assertThat(document.getCategoryIds()).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @DisplayName("빌더 패턴으로 BookSearchDocument 객체를 생성한다")
    void createBookSearchDocument_WithBuilder_Success() {
        // given
        Set<String> tags = Set.of("프로그래밍");
        Set<Long> categoryIds = Set.of(1L, 2L);

        // when
        BookSearchDocument document = BookSearchDocument.builder()
                .id("9788960777330")
                .isbn("9788960777330")
                .title("클린 코드")
                .author("로버트 C. 마틴")
                .tags(tags)
                .categoryIds(categoryIds)
                .sellingPrice(31500.0)
                .originalPrice(35000.0)
                .build();

        // then
        assertThat(document.getId()).isEqualTo("9788960777330");
        assertThat(document.getIsbn()).isEqualTo("9788960777330");
        assertThat(document.getTitle()).isEqualTo("클린 코드");
        assertThat(document.getAuthor()).isEqualTo("로버트 C. 마틴");
        assertThat(document.getTags()).containsExactly("프로그래밍");
        assertThat(document.getCategoryIds()).containsExactlyInAnyOrder(1L, 2L);
        assertThat(document.getSellingPrice()).isEqualTo(31500.0);
        assertThat(document.getOriginalPrice()).isEqualTo(35000.0);
    }

    @Test
    @DisplayName("Book 엔티티로부터 BookSearchDocument를 생성한다")
    void createBookSearchDocument_FromBook_Success() {
        // when
        BookSearchDocument document = BookSearchDocument.from(book);

        // then
        assertThat(document.getId()).isEqualTo("9788960777330");
        assertThat(document.getIsbn()).isEqualTo("9788960777330");
        assertThat(document.getTitle()).isEqualTo("클린 코드");
        assertThat(document.getTitleAutocomplete()).isEqualTo("클린 코드");
        assertThat(document.getDescription()).isEqualTo("애자일 소프트웨어 장인 정신");
        assertThat(document.getAuthor()).isEqualTo("로버트 C. 마틴");
        assertThat(document.getPublishedDate()).isEqualTo(LocalDate.of(2013, 12, 24));
        assertThat(document.getSellingPrice()).isEqualTo(31500.0);
        assertThat(document.getOriginalPrice()).isEqualTo(35000.0);
        assertThat(document.getImageUrl()).isEqualTo("http://image.aladin.co.kr/product/1953/63/cover/8960777331_1.jpg");
    }

    @Test
    @DisplayName("Book의 태그들이 BookSearchDocument에 올바르게 매핑된다")
    void createBookSearchDocument_FromBook_TagsMapping() {
        // when
        BookSearchDocument document = BookSearchDocument.from(book);

        // then
        assertThat(document.getTags()).containsExactly("프로그래밍");
    }

    @Test
    @DisplayName("Book의 카테고리들이 BookSearchDocument에 올바르게 매핑된다")
    void createBookSearchDocument_FromBook_CategoriesMapping() {
        // when
        BookSearchDocument document = BookSearchDocument.from(book);

        // then
        assertThat(document.getCategoryIds()).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @DisplayName("Book의 기본값들이 BookSearchDocument에 올바르게 설정된다")
    void createBookSearchDocument_FromBook_DefaultValues() {
        // when
        BookSearchDocument document = BookSearchDocument.from(book);

        // then
        assertThat(document.getAverageRating()).isEqualTo(0.0);
        assertThat(document.getReviewCount()).isEqualTo(0);
        assertThat(document.getViewCount()).isEqualTo(0);
        assertThat(document.getSearchCount()).isEqualTo(0);
        assertThat(document.getPopularity()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("태그가 없는 Book으로부터 BookSearchDocument를 생성한다")
    void createBookSearchDocument_FromBookWithoutTags_Success() {
        // given
        book.setBookTags(new HashSet<>());

        // when
        BookSearchDocument document = BookSearchDocument.from(book);

        // then
        assertThat(document.getTags()).isEmpty();
    }

    @Test
    @DisplayName("카테고리가 없는 Book으로부터 BookSearchDocument를 생성한다")
    void createBookSearchDocument_FromBookWithoutCategories_Success() {
        // given
        book.setBookCategories(new HashSet<>());

        // when
        BookSearchDocument document = BookSearchDocument.from(book);

        // then
        assertThat(document.getCategoryIds()).isEmpty();
    }

    @Test
    @DisplayName("BookSearchDocument의 모든 필드를 설정하고 조회할 수 있다")
    void setAndGetAllFields_Success() {
        // given
        BookSearchDocument document = new BookSearchDocument();
        Set<String> tags = Set.of("자바", "스프링");
        Set<Long> categoryIds = Set.of(3L, 4L);

        // when
        document.setId("testId");
        document.setIsbn("testIsbn");
        document.setTitle("테스트 제목");
        document.setTitleAutocomplete("테스트 제목");
        document.setTitleChosung("ㅌㅅㅌ ㅈㅁ");
        document.setDescription("테스트 설명");
        document.setAuthor("테스트 저자");
        document.setTags(tags);
        document.setPublishedDate(LocalDate.of(2023, 1, 1));
        document.setSellingPrice(20000.0);
        document.setOriginalPrice(25000.0);
        document.setAverageRating(4.2);
        document.setReviewCount(50);
        document.setViewCount(500);
        document.setSearchCount(30);
        document.setImageUrl("http://test.com/image.jpg");
        document.setPopularity(75.5);
        document.setCategoryIds(categoryIds);

        // then
        assertThat(document.getId()).isEqualTo("testId");
        assertThat(document.getIsbn()).isEqualTo("testIsbn");
        assertThat(document.getTitle()).isEqualTo("테스트 제목");
        assertThat(document.getTitleAutocomplete()).isEqualTo("테스트 제목");
        assertThat(document.getTitleChosung()).isEqualTo("ㅌㅅㅌ ㅈㅁ");
        assertThat(document.getDescription()).isEqualTo("테스트 설명");
        assertThat(document.getAuthor()).isEqualTo("테스트 저자");
        assertThat(document.getTags()).containsExactlyInAnyOrder("자바", "스프링");
        assertThat(document.getPublishedDate()).isEqualTo(LocalDate.of(2023, 1, 1));
        assertThat(document.getSellingPrice()).isEqualTo(20000.0);
        assertThat(document.getOriginalPrice()).isEqualTo(25000.0);
        assertThat(document.getAverageRating()).isEqualTo(4.2);
        assertThat(document.getReviewCount()).isEqualTo(50);
        assertThat(document.getViewCount()).isEqualTo(500);
        assertThat(document.getSearchCount()).isEqualTo(30);
        assertThat(document.getImageUrl()).isEqualTo("http://test.com/image.jpg");
        assertThat(document.getPopularity()).isEqualTo(75.5);
        assertThat(document.getCategoryIds()).containsExactlyInAnyOrder(3L, 4L);
    }

    @Test
    @DisplayName("복합 카테고리 계층구조가 올바르게 매핑된다")
    void createBookSearchDocument_FromBook_ComplexCategoryHierarchy() {
        // given
        Category grandChildCategory = new Category();
        grandChildCategory.setId(3L);
        grandChildCategory.setName("프로그래밍");
        grandChildCategory.setParentCategory(childCategory);

        BookCategory grandChildBookCategory = new BookCategory();
        grandChildBookCategory.setId(2L);
        grandChildBookCategory.setBook(book);
        grandChildBookCategory.setCategory(grandChildCategory);

        book.getBookCategories().add(grandChildBookCategory);

        // when
        BookSearchDocument document = BookSearchDocument.from(book);

        // then - 루트(1L), 자식(2L), 손자(3L) 카테고리 모두 포함
        assertThat(document.getCategoryIds()).containsExactlyInAnyOrder(1L, 2L, 3L);
    }

    @Test
    @DisplayName("null 값들을 포함한 Book으로부터 BookSearchDocument를 생성한다")
    void createBookSearchDocument_FromBookWithNullValues_Success() {
        // given
        Book bookWithNulls = new Book();
        bookWithNulls.setIsbn("testIsbn");
        bookWithNulls.setTitle(null);
        bookWithNulls.setDescription(null);
        bookWithNulls.setAuthor(null);
        bookWithNulls.setPublicationDate(null);
        bookWithNulls.setSellingPrice(null);
        bookWithNulls.setOriginalPrice(null);
        bookWithNulls.setImageUrl(null);
        bookWithNulls.setBookTags(new HashSet<>());
        bookWithNulls.setBookCategories(new HashSet<>());

        // when
        BookSearchDocument document = BookSearchDocument.from(bookWithNulls);

        // then
        assertThat(document.getIsbn()).isEqualTo("testIsbn");
        assertThat(document.getTitle()).isNull();
        assertThat(document.getDescription()).isNull();
        assertThat(document.getAuthor()).isNull();
        assertThat(document.getPublishedDate()).isNull();
        assertThat(document.getSellingPrice()).isNull();
        assertThat(document.getOriginalPrice()).isNull();
        assertThat(document.getImageUrl()).isNull();
        assertThat(document.getTags()).isEmpty();
        assertThat(document.getCategoryIds()).isEmpty();
    }
}