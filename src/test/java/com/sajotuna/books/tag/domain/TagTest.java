package com.sajotuna.books.tag.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TagTest {

    @Test
    @DisplayName("태그명을 가진 Tag 객체를 생성한다")
    void createTag_WithTagName_Success() {
        // given
        String tagName = "자바";

        // when
        Tag tag = new Tag(tagName);

        // then
        assertThat(tag.getTagName()).isEqualTo(tagName);
        assertThat(tag.getId()).isNull(); // 아직 영속화되지 않음
        assertThat(tag.getBookTags()).isNotNull();
        assertThat(tag.getBookTags()).isEmpty();
    }

    @Test
    @DisplayName("태그명을 설정하고 조회할 수 있다")
    void setAndGetTagName_Success() {
        // given
        Tag tag = new Tag("초기태그");
        String newTagName = "수정된태그";

        // when
        tag.setTagName(newTagName);

        // then
        assertThat(tag.getTagName()).isEqualTo(newTagName);
    }

    @Test
    @DisplayName("태그 ID를 설정하고 조회할 수 있다")
    void setAndGetId_Success() {
        // given
        Tag tag = new Tag("테스트태그");
        Long id = 1L;

        // when
        tag.setId(id);

        // then
        assertThat(tag.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("BookTags 컬렉션을 조회할 수 있다")
    void getBookTags_Success() {
        // given
        Tag tag = new Tag("테스트태그");

        // when
        Set<BookTag> bookTags = tag.getBookTags();

        // then
        assertThat(bookTags).isNotNull();
        assertThat(bookTags).isEmpty();
        assertThat(bookTags).isInstanceOf(Set.class);
    }

    @Test
    @DisplayName("BookTags 컬렉션을 설정할 수 있다")
    void setBookTags_Success() {
        // given
        Tag tag = new Tag("테스트태그");
        Set<BookTag> bookTags = Set.of(); // 빈 Set

        // when
        tag.setBookTags(bookTags);

        // then
        assertThat(tag.getBookTags()).isEqualTo(bookTags);
    }

    @Test
    @DisplayName("빈 태그명으로 태그를 생성할 수 있다")
    void createTag_WithEmptyTagName_Success() {
        // given
        String emptyTagName = "";

        // when
        Tag tag = new Tag(emptyTagName);

        // then
        assertThat(tag.getTagName()).isEqualTo(emptyTagName);
    }

    @Test
    @DisplayName("null 태그명으로 태그를 생성할 수 있다")
    void createTag_WithNullTagName_Success() {
        // given
        String nullTagName = null;

        // when
        Tag tag = new Tag(nullTagName);

        // then
        assertThat(tag.getTagName()).isNull();
    }

    @Test
    @DisplayName("긴 태그명으로 태그를 생성할 수 있다")
    void createTag_WithLongTagName_Success() {
        // given
        String longTagName = "이것은매우긴태그이름입니다정말로매우긴태그이름입니다";

        // when
        Tag tag = new Tag(longTagName);

        // then
        assertThat(tag.getTagName()).isEqualTo(longTagName);
    }
}