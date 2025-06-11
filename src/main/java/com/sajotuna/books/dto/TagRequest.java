package com.sajotuna.books.dto;

import jakarta.validation.constraints.NotBlank;

public record TagRequest(

        @NotBlank(message = "태그 이름은 필수 입력 항목입니다.")
        String tagName) {
}
