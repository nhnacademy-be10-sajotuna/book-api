package com.sajotuna.books.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRequest {
    private String keyword;
    private String sortBy; // popularity, new_arrivals, lowest_price, highest_price, rating, reviews
}