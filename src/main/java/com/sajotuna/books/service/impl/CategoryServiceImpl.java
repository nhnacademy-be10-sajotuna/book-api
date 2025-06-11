package com.sajotuna.books.service.impl;

import com.sajotuna.books.dto.CategoryRequest;
import com.sajotuna.books.dto.CategoryResponse;
import com.sajotuna.books.model.Category;
import com.sajotuna.books.repository.CategoryRepository;
import com.sajotuna.books.service.CategoryService;
import com.sajotuna.books.exception.CategoryNotFoundException; // 추가
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        Category parentCategory = null;
        if (categoryRequest.getParentId() != null) {
            parentCategory = categoryRepository.findById(categoryRequest.getParentId())
                    .orElseThrow(() -> new CategoryNotFoundException(categoryRequest.getParentId())); // 예외 변경
        }

        Category category = new Category(
                null,
                categoryRequest.getName(),
                parentCategory
        );

        Category savedCategory = categoryRepository.save(category);
        return new CategoryResponse(savedCategory);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::new)
                .collect(Collectors.toList());
    }
}