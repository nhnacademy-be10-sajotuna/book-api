// src/main/java/com/sajotuna/books/dto/TagRequest.java
package com.sajotuna.books.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TagRequest {
    private String name; // 생성할 태그 이름
}