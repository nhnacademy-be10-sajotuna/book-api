package com.sajotuna.books.category.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryCreateRequest {

    @NotBlank(message = "카테고리 이름은 필수 입력 항목입니다.")
    private String name;

    // 상위 카테고리 ID (선택 사항, 루트 카테고리인 경우 null)
    private Long parentCategoryId;
}