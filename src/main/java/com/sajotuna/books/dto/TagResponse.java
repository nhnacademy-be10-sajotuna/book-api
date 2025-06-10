// src/main/java/com/sajotuna/books/dto/TagResponse.java
package com.sajotuna.books.dto;

import com.sajotuna.books.model.Tag; // Tag 엔티티 임포트
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TagResponse {
    private Long id;   // 태그 ID
    private String name; // 태그 이름

    // Tag 엔티티를 받아서 TagResponse DTO로 변환하는 생성자
    public TagResponse(Tag tag) {
        this.id = tag.getId();
        this.name = tag.getName();
    }
}