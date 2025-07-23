package com.sajotuna.books.tag.domain;

import com.sajotuna.books.book.domain.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookTagTest {

    private Tag tag;
    private Book book;

    @BeforeEach
    void setUp() {
        tag = new Tag("자바");
        tag.setId(1L);
        
        book = new Book();
        book.setIsbn("9788960777330");
        book.setTitle("클린 코드");
    }

    @Test
    @DisplayName("Tag와 Book으로 BookTag 객체를 생성한다")
    void createBookTag_WithTagAndBook_Success() {
        // when
        BookTag bookTag = new BookTag(tag, book);

        // then
        assertThat(bookTag.getTag()).isEqualTo(tag);
        assertThat(bookTag.getBook()).isEqualTo(book);
        assertThat(bookTag.getId()).isNull(); // 아직 영속화되지 않음
    }

    @Test
    @DisplayName("BookTag의 ID를 조회할 수 있다")
    void getId_Success() {
        // given
        BookTag bookTag = new BookTag(tag, book);
        
        // when & then
        assertThat(bookTag.getId()).isNull(); // 초기값
    }

    @Test
    @DisplayName("BookTag의 Tag를 조회할 수 있다")
    void getTag_Success() {
        // given
        BookTag bookTag = new BookTag(tag, book);
        
        // when
        Tag retrievedTag = bookTag.getTag();
        
        // then
        assertThat(retrievedTag).isEqualTo(tag);
        assertThat(retrievedTag.getTagName()).isEqualTo("자바");
        assertThat(retrievedTag.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("BookTag의 Book을 조회할 수 있다")
    void getBook_Success() {
        // given
        BookTag bookTag = new BookTag(tag, book);
        
        // when
        Book retrievedBook = bookTag.getBook();
        
        // then
        assertThat(retrievedBook).isEqualTo(book);
        assertThat(retrievedBook.getIsbn()).isEqualTo("9788960777330");
        assertThat(retrievedBook.getTitle()).isEqualTo("클린 코드");
    }

    @Test
    @DisplayName("같은 Tag와 Book을 가진 BookTag는 동일하다")
    void equals_SameTagAndBook_ReturnsTrue() {
        // given
        BookTag bookTag1 = new BookTag(tag, book);
        BookTag bookTag2 = new BookTag(tag, book);
        
        // when & then
        assertThat(bookTag1).isEqualTo(bookTag2);
    }

    @Test
    @DisplayName("다른 Tag를 가진 BookTag는 동일하지 않다")
    void equals_DifferentTag_ReturnsFalse() {
        // given
        Tag differentTag = new Tag("스프링");
        differentTag.setId(2L);
        
        BookTag bookTag1 = new BookTag(tag, book);
        BookTag bookTag2 = new BookTag(differentTag, book);
        
        // when & then
        assertThat(bookTag1).isNotEqualTo(bookTag2);
    }

    @Test
    @DisplayName("다른 Book을 가진 BookTag는 동일하지 않다")
    void equals_DifferentBook_ReturnsFalse() {
        // given
        Book differentBook = new Book();
        differentBook.setIsbn("9788966262281");
        differentBook.setTitle("이펙티브 자바");
        
        BookTag bookTag1 = new BookTag(tag, book);
        BookTag bookTag2 = new BookTag(tag, differentBook);
        
        // when & then
        assertThat(bookTag1).isNotEqualTo(bookTag2);
    }

    @Test
    @DisplayName("자기 자신과는 동일하다")
    void equals_SameObject_ReturnsTrue() {
        // given
        BookTag bookTag = new BookTag(tag, book);
        
        // when & then
        assertThat(bookTag).isEqualTo(bookTag);
    }

    @Test
    @DisplayName("null과는 동일하지 않다")
    void equals_Null_ReturnsFalse() {
        // given
        BookTag bookTag = new BookTag(tag, book);
        
        // when & then
        assertThat(bookTag).isNotEqualTo(null);
    }

    @Test
    @DisplayName("다른 클래스 객체와는 동일하지 않다")
    void equals_DifferentClass_ReturnsFalse() {
        // given
        BookTag bookTag = new BookTag(tag, book);
        String otherObject = "다른 객체";
        
        // when & then
        assertThat(bookTag).isNotEqualTo(otherObject);
    }

    @Test
    @DisplayName("같은 Tag와 Book을 가진 BookTag는 같은 hashCode를 가진다")
    void hashCode_SameTagAndBook_ReturnsEqualHashCode() {
        // given
        BookTag bookTag1 = new BookTag(tag, book);
        BookTag bookTag2 = new BookTag(tag, book);
        
        // when & then
        assertThat(bookTag1.hashCode()).isEqualTo(bookTag2.hashCode());
    }

    @Test
    @DisplayName("다른 Tag나 Book을 가진 BookTag는 다른 hashCode를 가질 수 있다")
    void hashCode_DifferentTagOrBook_MayReturnDifferentHashCode() {
        // given
        Tag differentTag = new Tag("스프링");
        differentTag.setId(2L);
        
        BookTag bookTag1 = new BookTag(tag, book);
        BookTag bookTag2 = new BookTag(differentTag, book);
        
        // when & then
        // 다른 hashCode를 가질 가능성이 높지만, 반드시 다를 필요는 없음
        // 다만 equals가 false이면 가능한 한 다른 hashCode를 가져야 함
        assertThat(bookTag1.equals(bookTag2)).isFalse();
    }
}