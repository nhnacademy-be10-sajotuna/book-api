package com.sajotuna.books.book.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BookBatchRequest {

    @NotNull
    private List<String> isbns;
}
