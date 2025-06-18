// src/main/java/com/sajotuna/books/dto/LikeRequest.java
package com.sajotuna.books.dto;

import lombok.Getter; // 이 부분과
import lombok.NoArgsConstructor;
import lombok.Setter; // 이 부분이 올바르게 임포트되어 있는지 확인하세요

@Getter // 이 어노테이션이 반드시 있어야 합니다.
@Setter // 이 어노테이션도 함께 있는 것이 일반적입니다.
@NoArgsConstructor
public class LikeRequest {
    private String bookIsbn; // 필드 이름이 'bookIsbn'인지 정확히 확인
}