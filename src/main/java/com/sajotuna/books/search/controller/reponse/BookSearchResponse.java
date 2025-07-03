package com.sajotuna.books.search.controller.reponse;

import com.sajotuna.books.search.BookSearchDocument;

import java.time.LocalDate;
import java.util.Set; // Set 추가

public record BookSearchResponse(
        String isbn,
        String title,
        String author,
        Double sellingPrice,
        Double averageRating,
        String imageUrl,
        Double popularity,
        LocalDate publishedDate,
        Integer reviewCount,
        int searchCount,
        Set<String> categories // 새롭게 추가된 부분
) {
    public static BookSearchResponse from(BookSearchDocument bookSearchDocument) {
        return new BookSearchResponse(
                bookSearchDocument.getIsbn(),
                bookSearchDocument.getTitle(),
                bookSearchDocument.getAuthor(),
                bookSearchDocument.getSellingPrice(),
                bookSearchDocument.getAverageRating(),
                bookSearchDocument.getImageUrl(),
                bookSearchDocument.getPopularity(),
                bookSearchDocument.getPublishedDate(),
                bookSearchDocument.getReviewCount(),
                bookSearchDocument.getSearchCount(),
                bookSearchDocument.getCategories() // 새롭게 추가된 부분
        );
    }
}