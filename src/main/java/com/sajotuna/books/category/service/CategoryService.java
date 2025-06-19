package com.sajotuna.books.category.service;

import com.sajotuna.books.category.controller.reqest.CategoryRequest; // 추가
import com.sajotuna.books.category.controller.response.CategoryResponse; // 추가
import com.sajotuna.books.category.domain.Category;

import java.util.List;

public interface CategoryService {
    // createCategory 메서드 시그니처 변경: CategoryRequest를 인자로 받음
    CategoryResponse createCategory(CategoryRequest categoryRequest);

    // getAllCategories 메서드 시그니처 변경: List<CategoryResponse>를 반환
    List<CategoryResponse> getAllCategories();

    List<Category> findOrCreateCategories(List<String> categoryNames);

}