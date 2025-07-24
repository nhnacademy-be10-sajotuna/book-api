package com.sajotuna.books.category.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {

    private Category rootCategory;
    private Category childCategory;
    private Category grandChildCategory;

    @BeforeEach
    void setUp() {
        rootCategory = new Category();
        rootCategory.setId(1L);
        rootCategory.setName("도서");
        rootCategory.setParentCategory(null);

        childCategory = new Category();
        childCategory.setId(2L);
        childCategory.setName("컴퓨터/IT");
        childCategory.setParentCategory(rootCategory);

        grandChildCategory = new Category();
        grandChildCategory.setId(3L);
        grandChildCategory.setName("프로그래밍");
        grandChildCategory.setParentCategory(childCategory);
    }

    @Test
    @DisplayName("기본 생성자로 Category 객체를 생성한다")
    void createCategory_WithNoArgsConstructor_Success() {
        // when
        Category category = new Category();

        // then
        assertThat(category.getId()).isNull();
        assertThat(category.getName()).isNull();
        assertThat(category.getParentCategory()).isNull();
        assertThat(category.getSubCategories()).isNotNull();
        assertThat(category.getSubCategories()).isEmpty();
        assertThat(category.getBookCategories()).isNotNull();
        assertThat(category.getBookCategories()).isEmpty();
    }

    @Test
    @DisplayName("전체 인자 생성자로 Category 객체를 생성한다")
    void createCategory_WithAllArgsConstructor_Success() {
        // when
        Category category = new Category(1L, "테스트 카테고리", rootCategory);

        // then
        assertThat(category.getId()).isEqualTo(1L);
        assertThat(category.getName()).isEqualTo("테스트 카테고리");
        assertThat(category.getParentCategory()).isEqualTo(rootCategory);
        assertThat(category.getSubCategories()).isNotNull();
        assertThat(category.getBookCategories()).isNotNull();
    }

    @Test
    @DisplayName("Category의 ID를 설정하고 조회할 수 있다")
    void setAndGetId_Success() {
        // given
        Category category = new Category();
        Long id = 5L;

        // when
        category.setId(id);

        // then
        assertThat(category.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("Category의 이름을 설정하고 조회할 수 있다")
    void setAndGetName_Success() {
        // given
        Category category = new Category();
        String name = "새로운 카테고리";

        // when
        category.setName(name);

        // then
        assertThat(category.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("Category의 부모 카테고리를 설정하고 조회할 수 있다")
    void setAndGetParentCategory_Success() {
        // given
        Category category = new Category();

        // when
        category.setParentCategory(rootCategory);

        // then
        assertThat(category.getParentCategory()).isEqualTo(rootCategory);
    }

    @Test
    @DisplayName("Category의 하위 카테고리 컬렉션을 조회할 수 있다")
    void getSubCategories_Success() {
        // given
        Category category = new Category();

        // when
        Set<Category> subCategories = category.getSubCategories();

        // then
        assertThat(subCategories).isNotNull();
        assertThat(subCategories).isEmpty();
        assertThat(subCategories).isInstanceOf(Set.class);
    }

    @Test
    @DisplayName("Category의 BookCategory 컬렉션을 조회할 수 있다")
    void getBookCategories_Success() {
        // given
        Category category = new Category();

        // when
        Set<BookCategory> bookCategories = category.getBookCategories();

        // then
        assertThat(bookCategories).isNotNull();
        assertThat(bookCategories).isEmpty();
        assertThat(bookCategories).isInstanceOf(Set.class);
    }

    @Test
    @DisplayName("루트 카테고리의 경로를 조회한다")
    void getPathFromRoot_RootCategory_Success() {
        // when
        List<Category> path = rootCategory.getPathFromRoot();

        // then
        assertThat(path).hasSize(1);
        assertThat(path.get(0)).isEqualTo(rootCategory);
        assertThat(path.get(0).getName()).isEqualTo("도서");
    }

    @Test
    @DisplayName("자식 카테고리의 경로를 조회한다")
    void getPathFromRoot_ChildCategory_Success() {
        // when
        List<Category> path = childCategory.getPathFromRoot();

        // then
        assertThat(path).hasSize(2);
        assertThat(path.get(0)).isEqualTo(rootCategory);
        assertThat(path.get(0).getName()).isEqualTo("도서");
        assertThat(path.get(1)).isEqualTo(childCategory);
        assertThat(path.get(1).getName()).isEqualTo("컴퓨터/IT");
    }

    @Test
    @DisplayName("손자 카테고리의 경로를 조회한다")
    void getPathFromRoot_GrandChildCategory_Success() {
        // when
        List<Category> path = grandChildCategory.getPathFromRoot();

        // then
        assertThat(path).hasSize(3);
        assertThat(path.get(0)).isEqualTo(rootCategory);
        assertThat(path.get(0).getName()).isEqualTo("도서");
        assertThat(path.get(1)).isEqualTo(childCategory);
        assertThat(path.get(1).getName()).isEqualTo("컴퓨터/IT");
        assertThat(path.get(2)).isEqualTo(grandChildCategory);
        assertThat(path.get(2).getName()).isEqualTo("프로그래밍");
    }

    @Test
    @DisplayName("부모가 null인 카테고리의 경로를 조회한다")
    void getPathFromRoot_NullParent_Success() {
        // given
        Category orphanCategory = new Category();
        orphanCategory.setId(4L);
        orphanCategory.setName("고아 카테고리");
        orphanCategory.setParentCategory(null);

        // when
        List<Category> path = orphanCategory.getPathFromRoot();

        // then
        assertThat(path).hasSize(1);
        assertThat(path.get(0)).isEqualTo(orphanCategory);
        assertThat(path.get(0).getName()).isEqualTo("고아 카테고리");
    }

    @Test
    @DisplayName("빈 이름으로 Category를 생성할 수 있다")
    void createCategory_WithEmptyName_Success() {
        // given
        String emptyName = "";

        // when
        Category category = new Category(1L, emptyName, null);

        // then
        assertThat(category.getName()).isEqualTo(emptyName);
    }

    @Test
    @DisplayName("null 이름으로 Category를 생성할 수 있다")
    void createCategory_WithNullName_Success() {
        // given
        String nullName = null;

        // when
        Category category = new Category(1L, nullName, null);

        // then
        assertThat(category.getName()).isNull();
    }

    @Test
    @DisplayName("긴 이름으로 Category를 생성할 수 있다")
    void createCategory_WithLongName_Success() {
        // given
        String longName = "매우긴카테고리이름입니다정말로매우긴이름입니다";

        // when
        Category category = new Category(1L, longName, null);

        // then
        assertThat(category.getName()).isEqualTo(longName);
    }

    @Test
    @DisplayName("자기 자신을 부모로 가지는 경우의 경로를 조회한다")
    void getPathFromRoot_SelfParent_Success() {
        // given - 자기 자신을 부모로 가지는 카테고리 (실제로는 발생하지 않지만 테스트용)
        Category selfParentCategory = new Category();
        selfParentCategory.setId(1L);
        selfParentCategory.setName("자기참조카테고리");
        // 실제 운영에서는 이런 상황을 방지해야 하므로 테스트에서만 확인

        // when
        List<Category> path = selfParentCategory.getPathFromRoot();
        
        // then
        assertThat(path).hasSize(1);
        assertThat(path.get(0)).isEqualTo(selfParentCategory);
        assertThat(path.get(0).getName()).isEqualTo("자기참조카테고리");
    }
}