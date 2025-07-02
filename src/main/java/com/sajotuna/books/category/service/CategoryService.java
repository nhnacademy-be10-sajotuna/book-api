package com.sajotuna.books.category.service;


import com.sajotuna.books.category.controller.response.CategoryResponse;
import com.sajotuna.books.category.domain.Category;

import java.util.List;

public interface CategoryService {

    List<CategoryResponse> getAllCategories();

    List<Category> findOrCreateCategories(List<String> categoryNames);

    void deleteCategory(Long id); // 추가된 메서드
}