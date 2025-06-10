package com.sajotuna.books.service.impl;

import com.sajotuna.books.dto.CategoryRequest;
import com.sajotuna.books.dto.CategoryResponse;
import com.sajotuna.books.model.Category;
import com.sajotuna.books.repository.CategoryRepository;
import com.sajotuna.books.service.CategoryService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional // 트랜잭션 관리를 위해 추가 (필요에 따라)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        // 부모 카테고리 조회 (parent_id가 있을 경우)
        Category parentCategory = null;
        if (categoryRequest.getParentId() != null) {
            parentCategory = categoryRepository.findById(categoryRequest.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found with ID: " + categoryRequest.getParentId()));
        }

        // Category 엔티티 생성
        Category category = new Category(
                null, // ID는 자동 생성되므로 null
                categoryRequest.getName(),
                parentCategory
        );

        Category savedCategory = categoryRepository.save(category);
        return new CategoryResponse(savedCategory); // DTO로 변환하여 반환
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        // 모든 Category 엔티티를 조회하고, CategoryResponse DTO로 변환하여 반환
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::new) // Category 엔티티를 CategoryResponse DTO로 변환
                .collect(Collectors.toList());
    }
}