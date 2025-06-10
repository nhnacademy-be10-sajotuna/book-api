package com.nhnacademy.books.service;

import com.nhnacademy.books.model.Category;
import com.nhnacademy.books.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션, 쓰기는 별도 설정
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Category createCategory(String name, Long parentCategoryId) {
        Optional<Category> existingCategory = categoryRepository.findByName(name);
        if (existingCategory.isPresent()) {
            throw new IllegalArgumentException("카테고리 이름이 이미 존재합니다: " + name);
        }

        Category category;
        if (parentCategoryId != null) {
            Category parentCategory = categoryRepository.findById(parentCategoryId)
                    .orElseThrow(() -> new IllegalArgumentException("상위 카테고리를 찾을 수 없습니다. ID: " + parentCategoryId));
            category = new Category(name, parentCategory);
        } else {
            category = new Category(name);
        }
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Transactional
    public Category updateCategory(Long id, String newName, Long newParentCategoryId) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다. ID: " + id));

        // 이름 변경 (중복 확인)
        if (!category.getName().equals(newName)) {
            if (categoryRepository.findByName(newName).isPresent()) {
                throw new IllegalArgumentException("새로운 카테고리 이름이 이미 존재합니다: " + newName);
            }
            category.setName(newName);
        }

        // 상위 카테고리 변경
        if (newParentCategoryId != null) {
            Category newParentCategory = categoryRepository.findById(newParentCategoryId)
                    .orElseThrow(() -> new IllegalArgumentException("새로운 상위 카테고리를 찾을 수 없습니다. ID: " + newParentCategoryId));
            if (!category.equals(newParentCategory) && !newParentCategory.getSubCategories().contains(category)) { // 순환 참조 방지
                if (category.getParentCategory() != null) {
                    category.getParentCategory().getSubCategories().remove(category); // 기존 상위 카테고리에서 제거
                }
                category.setParentCategory(newParentCategory);
                newParentCategory.addSubCategory(category); // 새로운 상위 카테고리에 추가
            }
        } else if (category.getParentCategory() != null) { // 상위 카테고리를 제거하는 경우
            category.getParentCategory().getSubCategories().remove(category);
            category.setParentCategory(null);
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다. ID: " + id));

        // 하위 카테고리가 있다면 삭제 불가 (정책에 따라 변경 가능)
        if (!category.getSubCategories().isEmpty()) {
            throw new IllegalStateException("하위 카테고리가 있는 카테고리는 삭제할 수 없습니다.");
        }

        // 해당 카테고리를 참조하는 도서가 있을 경우 (ManyToMany이므로, 해당 도서에서 카테고리 연결을 끊어야 함)
        // 실제로는 BookService에서 도서 업데이트 시 카테고리 목록을 잘 관리해야 합니다.
        // 여기서는 간단히 카테고리만 삭제.
        categoryRepository.delete(category);
    }
}