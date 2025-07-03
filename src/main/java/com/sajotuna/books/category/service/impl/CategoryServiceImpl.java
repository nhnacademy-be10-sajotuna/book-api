package com.sajotuna.books.category.service.impl;

import com.sajotuna.books.category.controller.request.CategoryCreateRequest;
import com.sajotuna.books.category.controller.response.CategoryResponse;
import com.sajotuna.books.category.domain.Category;
import com.sajotuna.books.category.exception.CategoryNotFoundException;
import com.sajotuna.books.category.exception.DuplicateCategoryException;
import com.sajotuna.books.category.repository.CategoryRepository;
import com.sajotuna.books.category.service.CategoryService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page; // Page 임포트 추가
import org.springframework.data.domain.Pageable; // Pageable 임포트 추가
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

    // 모든 카테고리 조회 (페이지네이션 적용) (수정된 부분)
    @Override
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable) // Pageable 객체 사용
                .map(CategoryResponse::new);
    }

    // 카테고리 수동 등록 기능
    @Override
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        Category parentCategory = null;
        if (request.getParentCategoryId() != null) {
            parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.getParentCategoryId()));
        }

        // 동일한 부모 카테고리 밑에 동일한 이름의 카테고리가 있는지 확인
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
            parent = category; // 다음 루프에서 이 category가 부모가 됨
        }

        return result;
    }

    @Override
    public void deleteCategory(Long id) {
        // 카테고리가 존재하는지 확인
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(id);
        }
        // 하위 카테고리 또는 연결된 책이 있다면 삭제 정책에 따라 처리해야 합니다.
        // 현재는 단순히 삭제합니다. cascade 설정에 따라 관련 BookCategory도 삭제될 수 있습니다.
        categoryRepository.deleteById(id);
    }
}