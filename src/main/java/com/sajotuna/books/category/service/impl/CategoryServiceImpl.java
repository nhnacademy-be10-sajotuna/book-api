package com.sajotuna.books.category.service.impl;

import com.sajotuna.books.category.controller.request.CategoryCreateRequest;
import com.sajotuna.books.category.controller.response.CategoryResponse;
import com.sajotuna.books.category.domain.Category;
import com.sajotuna.books.category.exception.CategoryNotFoundException;
import com.sajotuna.books.category.exception.DuplicateCategoryException;
import com.sajotuna.books.category.repository.CategoryRepository;
import com.sajotuna.books.category.service.CategoryService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(CategoryResponse::new);
    }

    @Override
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        Category parentCategory = null;
        if (request.getParentCategoryId() != null) {
            parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.getParentCategoryId()));
        }

        if (categoryRepository.existsByNameAndParentCategory(request.getName(), parentCategory)) {
            throw new DuplicateCategoryException(request.getName(), request.getParentCategoryId());
        }

        Category newCategory = new Category();
        newCategory.setName(request.getName());
        newCategory.setParentCategory(parentCategory);

        Category savedCategory = categoryRepository.save(newCategory);
        return new CategoryResponse(savedCategory);
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
            parent = category;
        }

        return result;
    }

    @Override
    public void deleteCategory(Long id) {
        // 카테고리가 존재하는지 확인
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(id);
        }
        // Category 엔티티의 bookCategories 필드에 orphanRemoval = true 설정되어 있으므로
        // Category 삭제 시 연결된 BookCategory 엔티티들도 자동으로 삭제되어 도서와의 연결이 해제됩니다.
        categoryRepository.deleteById(id);
    }
}