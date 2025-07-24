package com.sajotuna.books.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.books.category.controller.request.CategoryCreateRequest;
import com.sajotuna.books.category.controller.response.CategoryResponse;
import com.sajotuna.books.category.exception.CategoryNotFoundException;
import com.sajotuna.books.category.exception.DuplicateCategoryException;
import com.sajotuna.books.category.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@ActiveProfiles("test")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("모든 카테고리를 페이징으로 조회한다")
    void getAllCategories_Success() throws Exception {
        // given
        CategoryResponse category1 = new CategoryResponse();
        category1.setId(1L);
        category1.setName("도서");
        
        CategoryResponse category2 = new CategoryResponse();
        category2.setId(2L);
        category2.setName("컴퓨터/IT");
        
        List<CategoryResponse> categories = Arrays.asList(category1, category2);
        Page<CategoryResponse> categoryPage = new PageImpl<>(categories, PageRequest.of(0, 10), categories.size());
        
        given(categoryService.getAllCategories(any(Pageable.class))).willReturn(categoryPage);

        // when & then
        mockMvc.perform(get("/api/categories")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("도서"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].name").value("컴퓨터/IT"));
    }

    @Test
    @DisplayName("부모 ID로 하위 카테고리를 조회한다")
    void getAllCategoriesByParent_Success() throws Exception {
        // given
        Long parentId = 1L;
        CategoryResponse subCategory = new CategoryResponse();
        subCategory.setId(2L);
        subCategory.setName("컴퓨터/IT");
        
        List<CategoryResponse> subCategories = Arrays.asList(subCategory);
        Page<CategoryResponse> categoryPage = new PageImpl<>(subCategories, PageRequest.of(0, 10), subCategories.size());
        
        given(categoryService.getAllCategoriesByParentId(any(Pageable.class), anyLong())).willReturn(categoryPage);

        // when & then
        mockMvc.perform(get("/api/categories/children")
                        .param("parentId", parentId.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.content[0].name").value("컴퓨터/IT"));
    }

    @Test
    @DisplayName("부모 ID 없이 하위 카테고리를 조회한다")
    void getAllCategoriesByParent_WithoutParentId_Success() throws Exception {
        // given
        CategoryResponse rootCategory = new CategoryResponse();
        rootCategory.setId(1L);
        rootCategory.setName("도서");
        
        List<CategoryResponse> rootCategories = Arrays.asList(rootCategory);
        Page<CategoryResponse> categoryPage = new PageImpl<>(rootCategories, PageRequest.of(0, 10), rootCategories.size());
        
        given(categoryService.getAllCategoriesByParentId(any(Pageable.class), any())).willReturn(categoryPage);

        // when & then
        mockMvc.perform(get("/api/categories/children")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    @DisplayName("카테고리의 부모 카테고리들을 조회한다")
    void getParentCategories_Success() throws Exception {
        // given
        Long categoryId = 3L;
        
        CategoryResponse parent1 = new CategoryResponse();
        parent1.setId(1L);
        parent1.setName("도서");
        
        CategoryResponse parent2 = new CategoryResponse();
        parent2.setId(2L);
        parent2.setName("컴퓨터/IT");
        
        CategoryResponse current = new CategoryResponse();
        current.setId(3L);
        current.setName("프로그래밍");
        
        List<CategoryResponse> parentCategories = Arrays.asList(parent1, parent2, current);
        
        given(categoryService.getParentCategories(anyLong())).willReturn(parentCategories);

        // when & then
        mockMvc.perform(get("/api/categories/parents")
                        .param("id", categoryId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("도서"))
                .andExpect(jsonPath("$[1].name").value("컴퓨터/IT"))
                .andExpect(jsonPath("$[2].name").value("프로그래밍"));
    }

    @Test
    @DisplayName("존재하지 않는 카테고리의 부모 조회시 404 오류가 발생한다")
    void getParentCategories_CategoryNotFound_NotFound() throws Exception {
        // given
        Long categoryId = 999L;
        given(categoryService.getParentCategories(anyLong())).willThrow(new CategoryNotFoundException(categoryId));

        // when & then
        mockMvc.perform(get("/api/categories/parents")
                        .param("id", categoryId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("새로운 카테고리를 생성한다")
    void createCategory_Success() throws Exception {
        // given
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName("새로운 카테고리");
        request.setParentCategoryId(1L);
        
        CategoryResponse response = new CategoryResponse();
        response.setId(4L);
        response.setName("새로운 카테고리");
        
        given(categoryService.createCategory(any(CategoryCreateRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.name").value("새로운 카테고리"));
    }

    @Test
    @DisplayName("빈 이름으로 카테고리 생성시 유효성 검증 오류가 발생한다")
    void createCategory_EmptyName_ValidationError() throws Exception {
        // given
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName("");
        request.setParentCategoryId(1L);

        // when & then
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("null 이름으로 카테고리 생성시 유효성 검증 오류가 발생한다")
    void createCategory_NullName_ValidationError() throws Exception {
        // given
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName(null);
        request.setParentCategoryId(1L);

        // when & then
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("존재하지 않는 부모 카테고리로 카테고리 생성시 404 오류가 발생한다")
    void createCategory_ParentNotFound_NotFound() throws Exception {
        // given
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName("새로운 카테고리");
        request.setParentCategoryId(999L);
        
        given(categoryService.createCategory(any(CategoryCreateRequest.class)))
                .willThrow(new CategoryNotFoundException(999L));

        // when & then
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("중복된 이름의 카테고리 생성시 409 오류가 발생한다")
    void createCategory_DuplicateName_Conflict() throws Exception {
        // given
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName("중복된 이름");
        request.setParentCategoryId(1L);
        
        given(categoryService.createCategory(any(CategoryCreateRequest.class)))
                .willThrow(new DuplicateCategoryException("중복된 이름", 1L));

        // when & then
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("카테고리를 삭제한다")
    void deleteCategory_Success() throws Exception {
        // given
        Long categoryId = 1L;
        doNothing().when(categoryService).deleteCategory(anyLong());

        // when & then
        mockMvc.perform(delete("/api/categories/{id}", categoryId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 삭제시 404 오류가 발생한다")
    void deleteCategory_NotFound_NotFound() throws Exception {
        // given
        Long categoryId = 999L;
        doThrow(new CategoryNotFoundException(categoryId)).when(categoryService).deleteCategory(anyLong());

        // when & then
        mockMvc.perform(delete("/api/categories/{id}", categoryId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("특정 카테고리의 모든 하위 카테고리를 조회한다")
    void getAllSubCategories_Success() throws Exception {
        // given
        Long categoryId = 1L;
        
        CategoryResponse subCategory1 = new CategoryResponse();
        subCategory1.setId(2L);
        subCategory1.setName("컴퓨터/IT");
        
        CategoryResponse subCategory2 = new CategoryResponse();
        subCategory2.setId(3L);
        subCategory2.setName("프로그래밍");
        
        List<CategoryResponse> subCategories = Arrays.asList(subCategory1, subCategory2);
        
        given(categoryService.getAllSubCategories(anyLong())).willReturn(subCategories);

        // when & then
        mockMvc.perform(get("/api/categories/{id}/subcategories", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("컴퓨터/IT"))
                .andExpect(jsonPath("$[1].id").value(3))
                .andExpect(jsonPath("$[1].name").value("프로그래밍"));
    }

    @Test
    @DisplayName("존재하지 않는 카테고리의 하위 카테고리 조회시 404 오류가 발생한다")
    void getAllSubCategories_CategoryNotFound_NotFound() throws Exception {
        // given
        Long categoryId = 999L;
        given(categoryService.getAllSubCategories(anyLong())).willThrow(new CategoryNotFoundException(categoryId));

        // when & then
        mockMvc.perform(get("/api/categories/{id}/subcategories", categoryId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("하위 카테고리가 없는 경우 빈 배열을 반환한다")
    void getAllSubCategories_NoSubCategories_ReturnsEmptyArray() throws Exception {
        // given
        Long categoryId = 3L;
        given(categoryService.getAllSubCategories(anyLong())).willReturn(Arrays.asList());

        // when & then
        mockMvc.perform(get("/api/categories/{id}/subcategories", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("카테고리 ID 파라미터 없이 부모 카테고리 조회시 오류가 발생한다")
    void getParentCategories_NoIdParam_Error() throws Exception {
        // when & then
        mockMvc.perform(get("/api/categories/parents"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("잘못된 형식의 카테고리 ID로 부모 카테고리 조회시 오류가 발생한다")
    void getParentCategories_InvalidIdFormat_Error() throws Exception {
        // when & then
        mockMvc.perform(get("/api/categories/parents")
                        .param("id", "invalid"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("잘못된 형식의 카테고리 ID로 삭제시 오류가 발생한다")
    void deleteCategory_InvalidIdFormat_Error() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/categories/{id}", "invalid"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("잘못된 형식의 카테고리 ID로 하위 카테고리 조회시 오류가 발생한다")
    void getAllSubCategories_InvalidIdFormat_Error() throws Exception {
        // when & then
        mockMvc.perform(get("/api/categories/{id}/subcategories", "invalid"))
                .andExpect(status().isInternalServerError());
    }
}