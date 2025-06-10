package com.sajotuna.books.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Lombok 어노테이션을 사용하여 Getter, Setter, 기본 생성자를 자동 생성
@Getter
@Setter
@NoArgsConstructor
public class CategoryRequest {
    private String name;        // 카테고리 이름
    private Long parentId;      // 부모 카테고리 ID (최상위 카테고리인 경우 null)
}