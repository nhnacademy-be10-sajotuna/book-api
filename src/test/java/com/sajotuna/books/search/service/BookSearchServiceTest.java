package com.sajotuna.books.search.service;

import com.sajotuna.books.book.exception.BookNotFoundException;
import com.sajotuna.books.category.exception.InvalidCategoryIdFormatException;
import com.sajotuna.books.search.BookSearchDocument;
import com.sajotuna.books.search.controller.reponse.BookSearchResponse;
import com.sajotuna.books.search.repository.BookSearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class BookSearchServiceTest {

    @Mock
    private ElasticsearchOperations operations;

    @Mock
    private BookSearchSynService bookSearchSynService;

    @Mock
    private BookSearchRepository bookSearchRepository;

    @InjectMocks
    private BookSearchService bookSearchService;

    private BookSearchDocument document1;
    private BookSearchDocument document2;

    @BeforeEach
    void setUp() {
        document1 = BookSearchDocument.builder()
                .id("9788960777330")
                .isbn("9788960777330")
                .title("클린 코드")
                .author("로버트 C. 마틴")
                .description("애자일 소프트웨어 장인 정신")
                .sellingPrice(31500.0)
                .originalPrice(35000.0)
                .publishedDate(LocalDate.of(2013, 12, 24))
                .averageRating(4.5)
                .reviewCount(100)
                .viewCount(1000)
                .searchCount(50)
                .popularity(85.5)
                .tags(Set.of("프로그래밍", "소프트웨어"))
                .categoryIds(Set.of(1L, 2L))
                .build();

        document2 = BookSearchDocument.builder()
                .id("9788966262281")
                .isbn("9788966262281")
                .title("이펙티브 자바")
                .author("조슈아 블로크")
                .description("자바 프로그래밍 가이드")
                .sellingPrice(36000.0)
                .originalPrice(40000.0)
                .publishedDate(LocalDate.of(2018, 11, 1))
                .averageRating(4.7)
                .reviewCount(150)
                .viewCount(1500)
                .searchCount(75)
                .popularity(92.5)
                .tags(Set.of("자바", "프로그래밍"))
                .categoryIds(Set.of(1L, 2L))
                .build();
    }

    @Test
    @DisplayName("키워드 없이 전체 검색을 수행한다")
    void search_WithoutKeyword_Success() {
        // given
        String keyword = null;
        String category = null;
        int page = 0;
        int size = 10;
        String sort = "popularity";
        Pageable pageable = PageRequest.of(page, size);

        SearchHits<BookSearchDocument> searchHits = createMockSearchHits(Arrays.asList(document1, document2), 2L);
        given(operations.search(any(Query.class), eq(BookSearchDocument.class))).willReturn(searchHits);

        // when
        Page<BookSearchResponse> result = bookSearchService.search(keyword, category, page, size, sort, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2L);
        assertThat(result.getContent().get(0).title()).isEqualTo("클린 코드");
        assertThat(result.getContent().get(1).title()).isEqualTo("이펙티브 자바");
        verify(bookSearchSynService).updateSearchStats(anyList());
    }

    @Test
    @DisplayName("키워드로 검색을 수행한다")
    void search_WithKeyword_Success() {
        // given
        String keyword = "클린 코드";
        String category = null;
        int page = 0;
        int size = 10;
        String sort = "popularity";
        Pageable pageable = PageRequest.of(page, size);

        SearchHits<BookSearchDocument> searchHits = createMockSearchHits(Arrays.asList(document1), 1L);
        given(operations.search(any(Query.class), eq(BookSearchDocument.class))).willReturn(searchHits);

        // when
        Page<BookSearchResponse> result = bookSearchService.search(keyword, category, page, size, sort, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("클린 코드");
        verify(bookSearchSynService).updateSearchStats(anyList());
    }

    @Test
    @DisplayName("초성 키워드로 검색을 수행한다")
    void search_WithChosungKeyword_Success() {
        // given
        String keyword = "ㅋㄹ";
        String category = null;
        int page = 0;
        int size = 10;
        String sort = "popularity";
        Pageable pageable = PageRequest.of(page, size);

        SearchHits<BookSearchDocument> searchHits = createMockSearchHits(Arrays.asList(document1), 1L);
        given(operations.search(any(Query.class), eq(BookSearchDocument.class))).willReturn(searchHits);

        // when
        Page<BookSearchResponse> result = bookSearchService.search(keyword, category, page, size, sort, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("클린 코드");
        verify(bookSearchSynService).updateSearchStats(anyList());
    }

    @Test
    @DisplayName("카테고리로 검색을 수행한다")
    void search_WithCategory_Success() {
        // given
        String keyword = null;
        String category = "1";
        int page = 0;
        int size = 10;
        String sort = "popularity";
        Pageable pageable = PageRequest.of(page, size);

        SearchHits<BookSearchDocument> searchHits = createMockSearchHits(Arrays.asList(document1, document2), 2L);
        given(operations.search(any(Query.class), eq(BookSearchDocument.class))).willReturn(searchHits);

        // when
        Page<BookSearchResponse> result = bookSearchService.search(keyword, category, page, size, sort, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        verify(bookSearchSynService).updateSearchStats(anyList());
    }

    @Test
    @DisplayName("잘못된 카테고리 ID로 검색시 예외가 발생한다")
    void search_WithInvalidCategory_ThrowsException() {
        // given
        String keyword = null;
        String category = "invalid";
        int page = 0;
        int size = 10;
        String sort = "popularity";
        Pageable pageable = PageRequest.of(page, size);

        // when & then
        assertThatThrownBy(() -> bookSearchService.search(keyword, category, page, size, sort, pageable))
                .isInstanceOf(InvalidCategoryIdFormatException.class);
    }

    @Test
    @DisplayName("키워드와 카테고리로 검색을 수행한다")
    void search_WithKeywordAndCategory_Success() {
        // given
        String keyword = "자바";
        String category = "2";
        int page = 0;
        int size = 10;
        String sort = "popularity";
        Pageable pageable = PageRequest.of(page, size);

        SearchHits<BookSearchDocument> searchHits = createMockSearchHits(Arrays.asList(document2), 1L);
        given(operations.search(any(Query.class), eq(BookSearchDocument.class))).willReturn(searchHits);

        // when
        Page<BookSearchResponse> result = bookSearchService.search(keyword, category, page, size, sort, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("이펙티브 자바");
        verify(bookSearchSynService).updateSearchStats(anyList());
    }

    @Test
    @DisplayName("다양한 정렬 옵션으로 검색을 수행한다")
    void search_WithDifferentSortOptions_Success() {
        // given
        String sort = "newest";
        SearchHits<BookSearchDocument> searchHits = createMockSearchHits(Arrays.asList(document1), 1L);
        given(operations.search(any(Query.class), eq(BookSearchDocument.class))).willReturn(searchHits);

        // when
        Page<BookSearchResponse> result = bookSearchService.search(null, null, 0, 10, sort, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        verify(bookSearchSynService).updateSearchStats(anyList());
    }

    @Test
    @DisplayName("제목 자동완성을 수행한다")
    void autoCompleteTitle_Success() {
        // given
        String keyword = "클린";

        SearchHits<BookSearchDocument> searchHits = createMockSearchHits(Arrays.asList(document1), 1L);
        given(operations.search(any(Query.class), eq(BookSearchDocument.class))).willReturn(searchHits);

        // when
        List<String> result = bookSearchService.autoCompleteTitle(keyword);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo("클린 코드");
    }

    @Test
    @DisplayName("초성으로 제목 자동완성을 수행한다")
    void autoCompleteTitle_WithChosung_Success() {
        // given
        String keyword = "ㅋㄹ";

        SearchHits<BookSearchDocument> searchHits = createMockSearchHits(Arrays.asList(document1), 1L);
        given(operations.search(any(Query.class), eq(BookSearchDocument.class))).willReturn(searchHits);

        // when
        List<String> result = bookSearchService.autoCompleteTitle(keyword);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo("클린 코드");
    }

    @Test
    @DisplayName("빈 키워드로 자동완성시 빈 목록을 반환한다")
    void autoCompleteTitle_EmptyKeyword_ReturnsEmptyList() {
        // given
        String keyword = "";

        // when
        List<String> result = bookSearchService.autoCompleteTitle(keyword);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("null 키워드로 자동완성시 빈 목록을 반환한다")
    void autoCompleteTitle_NullKeyword_ReturnsEmptyList() {
        // given
        String keyword = null;

        // when
        List<String> result = bookSearchService.autoCompleteTitle(keyword);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("ISBN으로 책을 검색한다")
    void searchByIsbn_Success() {
        // given
        String isbn = "9788960777330";
        given(bookSearchRepository.findById(isbn)).willReturn(Optional.of(document1));

        // when
        BookSearchResponse result = bookSearchService.searchByIsbn(isbn);

        // then
        assertThat(result.isbn()).isEqualTo(isbn);
        assertThat(result.title()).isEqualTo("클린 코드");
    }

    @Test
    @DisplayName("존재하지 않는 ISBN으로 검색시 예외가 발생한다")
    void searchByIsbn_NotFound_ThrowsException() {
        // given
        String isbn = "nonexistent";
        given(bookSearchRepository.findById(isbn)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookSearchService.searchByIsbn(isbn))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    @DisplayName("빈 ISBN으로 검색시 예외가 발생한다")
    void searchByIsbn_EmptyIsbn_ThrowsException() {
        // given
        String isbn = "";

        // when & then
        assertThatThrownBy(() -> bookSearchService.searchByIsbn(isbn))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ISBN은 필수입니다.");
    }

    @Test
    @DisplayName("null ISBN으로 검색시 예외가 발생한다")
    void searchByIsbn_NullIsbn_ThrowsException() {
        // given
        String isbn = null;

        // when & then
        assertThatThrownBy(() -> bookSearchService.searchByIsbn(isbn))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ISBN은 필수입니다.");
    }

    @Test
    @DisplayName("검색 결과가 없는 경우 빈 페이지를 반환한다")
    void search_NoResults_ReturnsEmptyPage() {
        // given
        String keyword = "존재하지않는책";
        String category = null;
        int page = 0;
        int size = 10;
        String sort = "popularity";
        Pageable pageable = PageRequest.of(page, size);

        SearchHits<BookSearchDocument> searchHits = createMockSearchHits(Arrays.asList(), 0L);
        given(operations.search(any(Query.class), eq(BookSearchDocument.class))).willReturn(searchHits);

        // when
        Page<BookSearchResponse> result = bookSearchService.search(keyword, category, page, size, sort, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0L);
        verify(bookSearchSynService).updateSearchStats(anyList());
    }

    @SuppressWarnings("unchecked")
    private SearchHits<BookSearchDocument> createMockSearchHits(List<BookSearchDocument> documents, long totalHits) {
        SearchHits<BookSearchDocument> searchHits = mock(SearchHits.class);
        
        List<SearchHit<BookSearchDocument>> searchHitList = documents.stream()
                .map(doc -> {
                    SearchHit<BookSearchDocument> hit = mock(SearchHit.class);
                    given(hit.getContent()).willReturn(doc);
                    return hit;
                })
                .toList();
        
        given(searchHits.getSearchHits()).willReturn(searchHitList);
        lenient().when(searchHits.getTotalHits()).thenReturn(totalHits);
        
        return searchHits;
    }
}