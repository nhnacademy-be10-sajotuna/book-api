package com.sajotuna.books.category.service.impl;

import com.sajotuna.books.category.controller.response.CategoryResponse;
import com.sajotuna.books.category.domain.Category;
import com.sajotuna.books.category.exception.CategoryNotFoundException; // 이 부분을 추가해야 합니다.
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

    @Override
    public void deleteCategory(Long id) {
        // 카테고리가 존재하는지 확인
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(id); // CategoryNotFoundException은 새롭게 생성해야 합니다.
        }
        // 하위 카테고리 또는 연결된 책이 있다면 삭제 정책에 따라 처리해야 합니다.
        // 현재는 단순히 삭제합니다. cascade 설정에 따라 관련 BookCategory도 삭제될 수 있습니다.
        categoryRepository.deleteById(id);
    }
}