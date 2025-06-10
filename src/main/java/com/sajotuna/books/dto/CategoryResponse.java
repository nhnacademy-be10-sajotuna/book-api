package com.sajotuna.books.dto;

import com.sajotuna.books.model.Category; // Category 엔티티 임포트
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private String parentCategoryName;
    private List<CategoryResponse> subCategories; // 하위 카테고리는 완전한 DTO 객체 리스트

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        if (category.getParentCategory() != null) {
            this.parentCategoryName = category.getParentCategory().getName();
        }
        // 중요: 하위 카테고리를 재귀적으로 CategoryResponse DTO로 변환
        if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
            this.subCategories = category.getSubCategories().stream()
                    .map(CategoryResponse::new) // <-- 이 부분을 CategoryResponse::new 로 변경!
                    .collect(Collectors.toList());
        }
    }

    // (기존에 id와 name만 받는 생성자가 있었다면 삭제해도 무방)
    // public CategoryResponse(Long id, String name) {
    //     this.id = id;
    //     this.name = name;
    // }
}