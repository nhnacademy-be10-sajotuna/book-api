package com.sajotuna.books.category.repository;

import com.sajotuna.books.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameAndParentCategory(String name, Category parentCategory);
}