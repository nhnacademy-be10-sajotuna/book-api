package com.sajotuna.books.category.service;


import com.sajotuna.books.category.controller.request.CategoryCreateRequest;
import com.sajotuna.books.category.controller.response.CategoryResponse;
import com.sajotuna.books.category.domain.Category;
import org.springframework.data.domain.Page; // Page 임포트 추가
import org.springframework.data.domain.Pageable; // Pageable 임포트 추가

import java.util.List;

public interface CategoryService {

    // 카테고리 목록 조회 (페이지네이션 적용) (수정된 부분)
    Page<CategoryResponse> getAllCategories(Pageable pageable);

    // 카테고리 수동 등록 기능
    CategoryResponse createCategory(CategoryCreateRequest request);

    List<Category> findOrCreateCategories(List<String> categoryNames);

    void deleteCategory(Long id);
}