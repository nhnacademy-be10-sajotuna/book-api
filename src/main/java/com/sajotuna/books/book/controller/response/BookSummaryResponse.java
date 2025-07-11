package com.sajotuna.books.book.controller.response;

import com.sajotuna.books.book.domain.Book;
import com.sajotuna.books.category.domain.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class BookSummaryResponse {
    private String isbn;
    private String title;
    private String imageUrl;
    private Double originalPrice;
    private Double sellingPrice;
    private Boolean giftWrappingAvailable;
    private List<Long> categoryIds;

    public static BookSummaryResponse from(Book book, List<Long> categoryIds) {
        return BookSummaryResponse.builder()
                .title(book.getTitle())
                .imageUrl(book.getImageUrl())
                .originalPrice(book.getOriginalPrice())
                .sellingPrice(book.getSellingPrice())
                .giftWrappingAvailable(book.getGiftWrappingAvailable())
                .isbn(book.getIsbn())
                .categoryIds(categoryIds)
                .build();
    }
}
