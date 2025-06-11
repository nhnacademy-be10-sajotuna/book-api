package com.sajotuna.books.controller;

import com.sajotuna.books.dto.CategoryRequest;
import com.sajotuna.books.dto.CategoryResponse;
import com.sajotuna.books.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 이 클래스가 REST 컨트롤러임을 선언
@RequestMapping("/api/categories") // 이 컨트롤러의 모든 API는 /api/categories로 시작
public class CategoryController {

    private final CategoryService categoryService; // CategoryService 주입

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 카테고리 등록
    // POST /api/categories
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest categoryRequest) {
        CategoryResponse createdCategory = categoryService.createCategory(categoryRequest);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED); // 201 Created 응답
    }

    // 모든 카테고리 조회
    // GET /api/categories
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories); // 200 OK 응답
    }

    // 필요하다면 여기에 특정 카테고리 조회, 수정, 삭제 등의 메서드를 추가할 수 있습니다.
}