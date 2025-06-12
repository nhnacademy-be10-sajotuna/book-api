package com.sajotuna.books.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewRequest(
        @NotNull Long userId,
        @NotBlank String bookIsbn,
        @Min(1) @Max(5) int rating, // 1점에서 5점 사이
        String comment
) {
}