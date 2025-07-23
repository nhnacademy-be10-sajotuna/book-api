package com.sajotuna.books.tag.service.impl;

import com.sajotuna.books.book.domain.Book;
import com.sajotuna.books.book.exception.BookNotFoundException;
import com.sajotuna.books.book.repository.BookRepository;
import com.sajotuna.books.tag.controller.request.BookTagRequest;
import com.sajotuna.books.tag.controller.response.BookTagResponse;
import com.sajotuna.books.tag.domain.BookTag;
import com.sajotuna.books.tag.domain.Tag;
import com.sajotuna.books.tag.exception.TagAlreadyExistsException;
import com.sajotuna.books.tag.exception.TagNotFoundException;
import com.sajotuna.books.tag.repository.BookTagRepository;
import com.sajotuna.books.tag.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookTagServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private BookTagRepository bookTagRepository;

    @InjectMocks
    private BookTagServiceImpl bookTagService;

    private Book book;
    private Tag tag;
    private BookTag bookTag;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setIsbn("9788960777330");
        book.setTitle("클린 코드");

        tag = new Tag("자바");
        tag.setId(1L);

        bookTag = new BookTag(tag, book);
    }

    @Test
    @DisplayName("책에 태그를 추가한다")
    void addTagToBook_Success() {
        // given
        BookTagRequest request = new BookTagRequest("9788960777330", 1L);
        
        given(bookRepository.findById(request.isbn())).willReturn(Optional.of(book));
        given(tagRepository.findById(request.tagId())).willReturn(Optional.of(tag));
        given(bookTagRepository.findByBook(book)).willReturn(Arrays.asList());
        given(bookTagRepository.save(any(BookTag.class))).willReturn(bookTag);

        // when
        BookTagResponse result = bookTagService.addTagToBook(request);

        // then
        // ID 검증은 생략 (영속화 레이어에서 처리)
        assertThat(result.tagId()).isEqualTo(1L);
        assertThat(result.tagName()).isEqualTo("자바");
        assertThat(result.isbn()).isEqualTo("9788960777330");
        
        verify(bookRepository).findById(request.isbn());
        verify(tagRepository).findById(request.tagId());
        verify(bookTagRepository).findByBook(book);
        verify(bookTagRepository).save(any(BookTag.class));
    }

    @Test
    @DisplayName("존재하지 않는 책에 태그 추가 시 예외가 발생한다")
    void addTagToBook_BookNotFound_ThrowsException() {
        // given
        BookTagRequest request = new BookTagRequest("없는ISBN", 1L);
        
        given(bookRepository.findById(request.isbn())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookTagService.addTagToBook(request))
                .isInstanceOf(BookNotFoundException.class);
        
        verify(bookRepository).findById(request.isbn());
        verify(tagRepository, never()).findById(any());
        verify(bookTagRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 태그를 책에 추가 시 예외가 발생한다")
    void addTagToBook_TagNotFound_ThrowsException() {
        // given
        BookTagRequest request = new BookTagRequest("9788960777330", 999L);
        
        given(bookRepository.findById(request.isbn())).willReturn(Optional.of(book));
        given(tagRepository.findById(request.tagId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookTagService.addTagToBook(request))
                .isInstanceOf(TagNotFoundException.class);
        
        verify(bookRepository).findById(request.isbn());
        verify(tagRepository).findById(request.tagId());
        verify(bookTagRepository, never()).save(any());
    }

    @Test
    @DisplayName("이미 존재하는 태그를 책에 추가 시 예외가 발생한다")
    void addTagToBook_TagAlreadyExists_ThrowsException() {
        // given
        BookTagRequest request = new BookTagRequest("9788960777330", 1L);
        
        given(bookRepository.findById(request.isbn())).willReturn(Optional.of(book));
        given(tagRepository.findById(request.tagId())).willReturn(Optional.of(tag));
        given(bookTagRepository.findByBook(book)).willReturn(Arrays.asList(bookTag));

        // when & then
        assertThatThrownBy(() -> bookTagService.addTagToBook(request))
                .isInstanceOf(TagAlreadyExistsException.class);
        
        verify(bookRepository).findById(request.isbn());
        verify(tagRepository).findById(request.tagId());
        verify(bookTagRepository).findByBook(book);
        verify(bookTagRepository, never()).save(any());
    }

    @Test
    @DisplayName("책에서 태그를 제거한다")
    void removeTagFromBook_Success() {
        // given
        BookTagRequest request = new BookTagRequest("9788960777330", 1L);
        
        given(bookRepository.findById(request.isbn())).willReturn(Optional.of(book));
        given(tagRepository.findById(request.tagId())).willReturn(Optional.of(tag));
        given(bookTagRepository.findByBook(book)).willReturn(Arrays.asList(bookTag));

        // when
        bookTagService.removeTagFromBook(request);

        // then
        verify(bookRepository).findById(request.isbn());
        verify(tagRepository).findById(request.tagId());
        verify(bookTagRepository).findByBook(book);
        verify(bookTagRepository).deleteAll(Arrays.asList(bookTag));
    }

    @Test
    @DisplayName("존재하지 않는 책에서 태그 제거 시 예외가 발생한다")
    void removeTagFromBook_BookNotFound_ThrowsException() {
        // given
        BookTagRequest request = new BookTagRequest("없는ISBN", 1L);
        
        given(bookRepository.findById(request.isbn())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookTagService.removeTagFromBook(request))
                .isInstanceOf(BookNotFoundException.class);
        
        verify(bookRepository).findById(request.isbn());
        verify(tagRepository, never()).findById(any());
        verify(bookTagRepository, never()).deleteAll(any());
    }

    @Test
    @DisplayName("존재하지 않는 태그를 책에서 제거 시 예외가 발생한다")
    void removeTagFromBook_TagNotFound_ThrowsException() {
        // given
        BookTagRequest request = new BookTagRequest("9788960777330", 999L);
        
        given(bookRepository.findById(request.isbn())).willReturn(Optional.of(book));
        given(tagRepository.findById(request.tagId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookTagService.removeTagFromBook(request))
                .isInstanceOf(TagNotFoundException.class);
        
        verify(bookRepository).findById(request.isbn());
        verify(tagRepository).findById(request.tagId());
        verify(bookTagRepository, never()).deleteAll(any());
    }

    @Test
    @DisplayName("책의 모든 태그를 조회한다")
    void getTagsByBook_Success() {
        // given
        String isbn = "9788960777330";
        Tag tag2 = new Tag("스프링");
        tag2.setId(2L);
        BookTag bookTag2 = new BookTag(tag2, book);
        // ID는 영속화 시 자동 생성됨
        
        List<BookTag> bookTags = Arrays.asList(bookTag, bookTag2);
        
        given(bookRepository.findById(isbn)).willReturn(Optional.of(book));
        given(bookTagRepository.findByBook(book)).willReturn(bookTags);

        // when
        List<BookTagResponse> result = bookTagService.getTagsByBook(isbn);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(BookTagResponse::tagName)
                .containsExactly("자바", "스프링");
        assertThat(result).extracting(BookTagResponse::isbn)
                .containsOnly("9788960777330");
        
        verify(bookRepository).findById(isbn);
        verify(bookTagRepository).findByBook(book);
    }

    @Test
    @DisplayName("존재하지 않는 책의 태그 조회 시 예외가 발생한다")
    void getTagsByBook_BookNotFound_ThrowsException() {
        // given
        String isbn = "없는ISBN";
        
        given(bookRepository.findById(isbn)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookTagService.getTagsByBook(isbn))
                .isInstanceOf(BookNotFoundException.class);
        
        verify(bookRepository).findById(isbn);
        verify(bookTagRepository, never()).findByBook(any());
    }

    @Test
    @DisplayName("태그가 없는 책의 태그 조회 시 빈 리스트를 반환한다")
    void getTagsByBook_NoTags_ReturnsEmptyList() {
        // given
        String isbn = "9788960777330";
        
        given(bookRepository.findById(isbn)).willReturn(Optional.of(book));
        given(bookTagRepository.findByBook(book)).willReturn(Arrays.asList());

        // when
        List<BookTagResponse> result = bookTagService.getTagsByBook(isbn);

        // then
        assertThat(result).isEmpty();
        
        verify(bookRepository).findById(isbn);
        verify(bookTagRepository).findByBook(book);
    }

    @Test
    @DisplayName("책에서 연결되지 않은 태그 제거 시 아무것도 삭제되지 않는다")
    void removeTagFromBook_TagNotLinked_NothingDeleted() {
        // given
        BookTagRequest request = new BookTagRequest("9788960777330", 1L);
        Tag differentTag = new Tag("다른태그");
        differentTag.setId(2L);
        BookTag differentBookTag = new BookTag(differentTag, book);
        
        given(bookRepository.findById(request.isbn())).willReturn(Optional.of(book));
        given(tagRepository.findById(request.tagId())).willReturn(Optional.of(tag));
        given(bookTagRepository.findByBook(book)).willReturn(Arrays.asList(differentBookTag));

        // when
        bookTagService.removeTagFromBook(request);

        // then
        verify(bookRepository).findById(request.isbn());
        verify(tagRepository).findById(request.tagId());
        verify(bookTagRepository).findByBook(book);
        verify(bookTagRepository).deleteAll(Arrays.asList()); // 빈 리스트
    }
}