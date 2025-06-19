package com.sajotuna.books.like.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LikeRequest {
    @NotBlank(message = "bookIsbn은 필수입니다.")
    private String bookIsbn;
}