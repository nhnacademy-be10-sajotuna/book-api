package com.sajotuna.books.category.repository;

import com.sajotuna.books.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameAndParentCategory(String name, Category parentCategory);

    // 동일한 부모 카테고리 아래에 같은 이름의 카테고리가 존재하는지 확인 (추가된 부분)
    boolean existsByNameAndParentCategory(String name, Category parentCategory);
}