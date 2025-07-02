package com.sajotuna.books.book.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter // BookCreateRequest를 BookUpdateReques로도 재활용하기 위해 Setter 유지
@NoArgsConstructor
public class BookCreateRequest {

    @NotBlank(message = "ISBN은 필수 입력 항목입니다.")
    private String isbn;

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    private String title;

    @NotBlank(message = "저자는 필수 입력 항목입니다.")
    private String author;

    @NotBlank(message = "출판사는 필수 입력 항목입니다.")
    private String publisher;

    @NotNull(message = "출판일은 필수 입력 항목입니다.")
    private LocalDate publicationDate;

    @PositiveOrZero(message = "페이지 수는 0 이상이어야 합니다.")
    private Integer pageCount;

    private String imageUrl;
    private String description;

    @NotNull(message = "정가는 필수 입력 항목입니다.")
    @PositiveOrZero(message = "정가는 0 이상이어야 합니다.")
    private Double originalPrice;

    @NotNull(message = "판매가는 필수 입력 항목입니다.")
    @PositiveOrZero(message = "판매가는 0 이상이어야 합니다.")
    private Double sellingPrice;

    @PositiveOrZero(message = "재고는 0 이상이어야 합니다.")
    private Integer stock = 0; // 재고 필드 추가

    private Boolean giftWrappingAvailable = false; // 기본값 제공
    private Integer likes = 0; // 기본값 제공

    private List<String> categoryNames; // 카테고리 이름 목록 (계층 구조)
    private Set<String> tagNames; // 태그 이름 목록
}