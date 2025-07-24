package com.sajotuna.books.category.service.impl;

import com.sajotuna.books.category.controller.request.CategoryCreateRequest;
import com.sajotuna.books.category.controller.response.CategoryResponse;
import com.sajotuna.books.category.domain.Category;
import com.sajotuna.books.category.exception.CategoryNotFoundException;
import com.sajotuna.books.category.exception.DuplicateCategoryException;
import com.sajotuna.books.category.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

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
    @DisplayName("모든 카테고리를 페이징으로 조회한다")
    void getAllCategories_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = Arrays.asList(rootCategory, childCategory);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        given(categoryRepository.findAll(pageable)).willReturn(categoryPage);

        // when
        Page<CategoryResponse> result = categoryService.getAllCategories(pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getName()).isEqualTo("도서");
        assertThat(result.getContent().get(1).getName()).isEqualTo("컴퓨터/IT");
        verify(categoryRepository).findAll(pageable);
    }

    @Test
    @DisplayName("부모 ID로 하위 카테고리를 페이징으로 조회한다")
    void getAllCategoriesByParentId_Success() {
        // given
        Long parentId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> subCategories = Arrays.asList(childCategory);
        Page<Category> categoryPage = new PageImpl<>(subCategories, pageable, subCategories.size());

        given(categoryRepository.findByParentCategoryId(pageable, parentId)).willReturn(categoryPage);

        // when
        Page<CategoryResponse> result = categoryService.getAllCategoriesByParentId(pageable, parentId);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("컴퓨터/IT");
        verify(categoryRepository).findByParentCategoryId(pageable, parentId);
    }

    @Test
    @DisplayName("카테고리의 부모 카테고리들을 조회한다")
    void getParentCategories_Success() {
        // given
        Long categoryId = 3L;

        given(categoryRepository.findById(3L)).willReturn(Optional.of(grandChildCategory));
        given(categoryRepository.findById(2L)).willReturn(Optional.of(childCategory));
        given(categoryRepository.findById(1L)).willReturn(Optional.of(rootCategory));

        // when
        List<CategoryResponse> result = categoryService.getParentCategories(categoryId);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getName()).isEqualTo("도서");
        assertThat(result.get(1).getName()).isEqualTo("컴퓨터/IT");
        assertThat(result.get(2).getName()).isEqualTo("프로그래밍");
    }

    @Test
    @DisplayName("존재하지 않는 카테고리의 부모 카테고리 조회시 예외가 발생한다")
    void getParentCategories_CategoryNotFound_ThrowsException() {
        // given
        Long nonExistentId = 999L;
        given(categoryRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> categoryService.getParentCategories(nonExistentId))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    @DisplayName("새로운 카테고리를 생성한다")
    void createCategory_Success() {
        // given
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName("새로운 카테고리");
        request.setParentCategoryId(1L);

        Category newCategory = new Category();
        newCategory.setId(4L);
        newCategory.setName("새로운 카테고리");
        newCategory.setParentCategory(rootCategory);

        given(categoryRepository.findById(1L)).willReturn(Optional.of(rootCategory));
        given(categoryRepository.existsByNameAndParentCategory(request.getName(), rootCategory)).willReturn(false);
        given(categoryRepository.save(any(Category.class))).willReturn(newCategory);

        // when
        CategoryResponse result = categoryService.createCategory(request);

        // then
        assertThat(result.getId()).isEqualTo(4L);
        assertThat(result.getName()).isEqualTo("새로운 카테고리");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("부모 카테고리 없이 루트 카테고리를 생성한다")
    void createCategory_WithoutParent_Success() {
        // given
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName("루트 카테고리");
        request.setParentCategoryId(null);

        Category newCategory = new Category();
        newCategory.setId(5L);
        newCategory.setName("루트 카테고리");
        newCategory.setParentCategory(null);

        given(categoryRepository.existsByNameAndParentCategory(request.getName(), null)).willReturn(false);
        given(categoryRepository.save(any(Category.class))).willReturn(newCategory);

        // when
        CategoryResponse result = categoryService.createCategory(request);

        // then
        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getName()).isEqualTo("루트 카테고리");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("존재하지 않는 부모 카테고리로 카테고리 생성시 예외가 발생한다")
    void createCategory_ParentNotFound_ThrowsException() {
        // given
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName("새로운 카테고리");
        request.setParentCategoryId(999L);

        given(categoryRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> categoryService.createCategory(request))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    @DisplayName("중복된 이름의 카테고리 생성시 예외가 발생한다")
    void createCategory_DuplicateName_ThrowsException() {
        // given
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName("컴퓨터/IT");
        request.setParentCategoryId(1L);

        given(categoryRepository.findById(1L)).willReturn(Optional.of(rootCategory));
        given(categoryRepository.existsByNameAndParentCategory(request.getName(), rootCategory)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> categoryService.createCategory(request))
                .isInstanceOf(DuplicateCategoryException.class);
    }

    @Test
    @DisplayName("카테고리 이름 목록으로 카테고리들을 찾거나 생성한다")
    void findOrCreateCategories_Success() {
        // given
        List<String> categoryNames = Arrays.asList("도서", "컴퓨터/IT", "프로그래밍");

        given(categoryRepository.findByNameAndParentCategory("도서", null))
                .willReturn(Optional.of(rootCategory));
        given(categoryRepository.findByNameAndParentCategory("컴퓨터/IT", rootCategory))
                .willReturn(Optional.of(childCategory));
        given(categoryRepository.findByNameAndParentCategory("프로그래밍", childCategory))
                .willReturn(Optional.of(grandChildCategory));

        // when
        List<Category> result = categoryService.findOrCreateCategories(categoryNames);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getName()).isEqualTo("도서");
        assertThat(result.get(1).getName()).isEqualTo("컴퓨터/IT");
        assertThat(result.get(2).getName()).isEqualTo("프로그래밍");
    }

    @Test
    @DisplayName("존재하지 않는 카테고리는 새로 생성한다")
    void findOrCreateCategories_CreateNew_Success() {
        // given
        List<String> categoryNames = Arrays.asList("새로운카테고리");

        Category newCategory = new Category();
        newCategory.setId(10L);
        newCategory.setName("새로운카테고리");

        given(categoryRepository.findByNameAndParentCategory("새로운카테고리", null))
                .willReturn(Optional.empty());
        given(categoryRepository.save(any(Category.class))).willReturn(newCategory);

        // when
        List<Category> result = categoryService.findOrCreateCategories(categoryNames);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("새로운카테고리");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("카테고리 ID 목록으로 카테고리들을 조회한다")
    void findAllByCategoryIds_Success() {
        // given
        List<Long> categoryIds = Arrays.asList(1L, 2L);
        List<Category> categories = Arrays.asList(rootCategory, childCategory);

        given(categoryRepository.findByIdIn(categoryIds)).willReturn(Optional.of(categories));

        // when
        List<Category> result = categoryService.findAllByCategoryIds(categoryIds);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("도서");
        assertThat(result.get(1).getName()).isEqualTo("컴퓨터/IT");
    }

    @Test
    @DisplayName("카테고리를 삭제한다")
    void deleteCategory_Success() {
        // given
        Long categoryId = 1L;
        given(categoryRepository.existsById(categoryId)).willReturn(true);
        willDoNothing().given(categoryRepository).deleteById(categoryId);

        // when
        categoryService.deleteCategory(categoryId);

        // then
        verify(categoryRepository).existsById(categoryId);
        verify(categoryRepository).deleteById(categoryId);
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 삭제시 예외가 발생한다")
    void deleteCategory_NotFound_ThrowsException() {
        // given
        Long categoryId = 999L;
        given(categoryRepository.existsById(categoryId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> categoryService.deleteCategory(categoryId))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    @DisplayName("특정 카테고리의 모든 하위 카테고리를 조회한다")
    void getAllSubCategories_Success() {
        // given
        Long categoryId = 1L;
        List<Category> directSubCategories = Arrays.asList(childCategory);
        List<Category> grandSubCategories = Arrays.asList(grandChildCategory);

        given(categoryRepository.existsById(categoryId)).willReturn(true);
        given(categoryRepository.findByParentCategoryId(1L)).willReturn(directSubCategories);
        given(categoryRepository.findByParentCategoryId(2L)).willReturn(grandSubCategories);
        given(categoryRepository.findByParentCategoryId(3L)).willReturn(Arrays.asList());

        // when
        List<CategoryResponse> result = categoryService.getAllSubCategories(categoryId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("컴퓨터/IT");
        assertThat(result.get(1).getName()).isEqualTo("프로그래밍");
    }

    @Test
    @DisplayName("존재하지 않는 카테고리의 하위 카테고리 조회시 예외가 발생한다")
    void getAllSubCategories_CategoryNotFound_ThrowsException() {
        // given
        Long categoryId = 999L;
        given(categoryRepository.existsById(categoryId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> categoryService.getAllSubCategories(categoryId))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    @DisplayName("하위 카테고리가 없는 경우 빈 목록을 반환한다")
    void getAllSubCategories_NoSubCategories_ReturnsEmptyList() {
        // given
        Long categoryId = 3L;
        given(categoryRepository.existsById(categoryId)).willReturn(true);
        given(categoryRepository.findByParentCategoryId(categoryId)).willReturn(Arrays.asList());

        // when
        List<CategoryResponse> result = categoryService.getAllSubCategories(categoryId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("빈 카테고리 이름 목록으로 빈 결과를 반환한다")
    void findOrCreateCategories_EmptyList_ReturnsEmpty() {
        // given
        List<String> emptyNames = Arrays.asList();

        // when
        List<Category> result = categoryService.findOrCreateCategories(emptyNames);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("빈 카테고리 ID 목록으로 빈 결과를 반환한다")
    void findAllByCategoryIds_EmptyList_ReturnsEmpty() {
        // given
        List<Long> emptyIds = Arrays.asList();
        given(categoryRepository.findByIdIn(emptyIds)).willReturn(Optional.of(Arrays.asList()));

        // when
        List<Category> result = categoryService.findAllByCategoryIds(emptyIds);

        // then
        assertThat(result).isEmpty();
    }
}