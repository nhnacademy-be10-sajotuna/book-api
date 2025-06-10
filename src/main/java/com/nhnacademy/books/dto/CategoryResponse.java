package com.nhnacademy.books.dto;

import com.nhnacademy.books.model.Category;
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
    // private CategoryResponse parentCategory; // 순환 참조를 끊기 위해 여기서는 포함하지 않거나, ID만 포함
    private String parentCategoryName; // 상위 카테고리 이름만 필요하다면
    private List<CategoryResponse> subCategories; // 하위 카테고리는 포함 가능

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        if (category.getParentCategory() != null) {
            this.parentCategoryName = category.getParentCategory().getName();
            // 또는 this.parentCategory = new CategoryResponse(category.getParentCategory()); // 이렇게 하면 다시 무한 재귀
            // 따라서, DTO에서는 필요한 정보만 포함하도록 설계해야 함
        }
        // 하위 카테고리는 필요한 경우에만 재귀적으로 DTO 변환
        if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
            this.subCategories = category.getSubCategories().stream()
                    .map(subCat -> new CategoryResponse(subCat.getId(), subCat.getName())) // 재귀 깊이 제한
                    .collect(Collectors.toList());
        }
    }

    // 하위 카테고리 DTO 생성을 위한 생성자 (재귀 깊이를 제한하기 위함)
    public CategoryResponse(Long id, String name) {
        this.id = id;
        this.name = name;
        // subCategories는 여기서는 초기화하지 않음 (단일 레벨만 가져옴)
    }
}