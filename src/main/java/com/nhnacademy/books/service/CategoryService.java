package com.nhnacademy.books.service;

import com.nhnacademy.books.dto.CategoryRequest; // 추가
import com.nhnacademy.books.dto.CategoryResponse; // 추가

import java.util.List;

public interface CategoryService {
    // createCategory 메서드 시그니처 변경: CategoryRequest를 인자로 받음
    CategoryResponse createCategory(CategoryRequest categoryRequest);

    // getAllCategories 메서드 시그니처 변경: List<CategoryResponse>를 반환
    List<CategoryResponse> getAllCategories();
}