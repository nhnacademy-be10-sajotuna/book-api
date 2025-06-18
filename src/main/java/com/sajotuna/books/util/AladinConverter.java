package com.sajotuna.books.util;

import com.sajotuna.books.dto.AladinBookResponse;
import com.sajotuna.books.dto.BookRequest;
import com.sajotuna.books.model.Book;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AladinConverter {

    // Request로 변환, 나중에 관리자가 직접 책 등록 해야 할 경우 사용.?
    public static BookRequest toBookRequest(AladinBookResponse aladinBookResponse) {
        BookRequest bookRequest = new BookRequest();
        bookRequest.setIsbn(
                aladinBookResponse.getIsbn13() != null && !aladinBookResponse.getIsbn13().isEmpty()
                        ? aladinBookResponse.getIsbn13()
                        : aladinBookResponse.getIsbn()
        );
        bookRequest.setTitle(aladinBookResponse.getTitle());
        bookRequest.setAuthor(aladinBookResponse.getAuthor());
        bookRequest.setPublisher(aladinBookResponse.getPublisher());
        bookRequest.setPublicationDate(parseDate(aladinBookResponse.getPubDate()));
        bookRequest.setDescription(aladinBookResponse.getDescription());
        bookRequest.setImageUrl(aladinBookResponse.getCover());
        bookRequest.setOriginalPrice(toDouble(aladinBookResponse.getPriceStandard()));
        bookRequest.setSellingPrice(toDouble(aladinBookResponse.getPriceStandard())); // 수정: Sales → Standard?
        bookRequest.setGiftWrappingAvailable(false);
        bookRequest.setLikes(0);
        bookRequest.setPageCount(0);
        bookRequest.setCategoryIds(null);
        bookRequest.setTagIds(null);
        return bookRequest;
    }

    // Book 엔티티로 변환
    public static List<Book> toBookEntityList(List<AladinBookResponse> responses) {
        return responses.stream()
                .map(response -> {
                    Book book = new Book();
                    book.setIsbn(response.getIsbn13() != null ? response.getIsbn13() : response.getIsbn());
                    book.setTitle(response.getTitle());
                    book.setAuthor(response.getAuthor());
                    book.setPublisher(response.getPublisher());
                    book.setPublicationDate(parseDate(response.getPubDate()));
                    book.setImageUrl(response.getCover() != null ? response.getCover() : "");
                    book.setDescription(response.getDescription() != null ? response.getDescription() : "");
                    book.setOriginalPrice(toDouble(response.getPriceStandard()));
                    book.setSellingPrice(toDouble(response.getPriceSales() != null ? response.getPriceSales() : response.getPriceStandard()));
                    book.setPageCount(0);
                    book.setLikes(0);
                    book.setGiftWrappingAvailable(false);
                    return book;
                })
                .toList();
    }


    private static LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            return null;
        }
    }

    private static Double toDouble(Integer value) {
        return value != null ? value.doubleValue() : null;
    }
}
