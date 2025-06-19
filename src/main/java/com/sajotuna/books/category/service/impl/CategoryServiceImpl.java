package com.sajotuna.books.category.service.impl;

import com.sajotuna.books.category.controller.reqest.CategoryRequest;
import com.sajotuna.books.category.controller.response.CategoryResponse;
import com.sajotuna.books.category.exception.CategoryNotFoundException; // 변경
import com.sajotuna.books.category.domain.Category;
import com.sajotuna.books.category.repository.CategoryRepository;
import com.sajotuna.books.category.service.CategoryService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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


    @Override
    public List<Category> findOrCreateCategories(List<String> categoryNames) {
        List<Category> result = new ArrayList<>();
        Category parent = null;

        for (String name : categoryNames) {
            Category category = categoryRepository.findByNameAndParentCategory(name, parent).orElse(null);

            if (category == null) {
                Category newCategory = new Category();
                newCategory.setName(name);
                newCategory.setParentCategory(parent);
                category = categoryRepository.save(newCategory);
            }

            result.add(category);
            parent = category; // 다음 루프에서 이 category가 부모가 됨
        }

        return result;
    }

}