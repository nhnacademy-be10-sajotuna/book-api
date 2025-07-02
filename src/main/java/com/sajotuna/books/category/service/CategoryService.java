package com.sajotuna.books.category.service;


import com.sajotuna.books.category.controller.request.CategoryCreateRequest;
import com.sajotuna.books.category.controller.response.CategoryResponse;
import com.sajotuna.books.category.domain.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    Page<CategoryResponse> getAllCategories(Pageable pageable);

    CategoryResponse createCategory(CategoryCreateRequest request);

    List<Category> findOrCreateCategories(List<String> categoryNames);

    // 해당 카테고리에 속한 도서들은 카테고리 삭제와 동시에 연결을 해제 시킨다.
    void deleteCategory(Long id);
}