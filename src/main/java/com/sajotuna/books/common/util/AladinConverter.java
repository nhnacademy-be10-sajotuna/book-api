package com.sajotuna.books.common.util;

import com.sajotuna.books.book.controller.response.AladinBookResponse;
import com.sajotuna.books.book.domain.Book;
import com.sajotuna.books.category.domain.BookCategory;
import com.sajotuna.books.category.domain.Category;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AladinConverter {

    // Book 하나를 생성 + 해당 Book과 연결할 Category 리스트 전달
    public static Book toBookEntity(AladinBookResponse response, Category category) {
        Book book = new Book();
        book.setIsbn(response.getIsbn13() != null ? response.getIsbn13() : response.getIsbn());
        book.setTitle(response.getTitle());
        book.setAuthor(response.getAuthor());
        book.setPublisher(response.getPublisher());
        book.setPublicationDate(parseDate(response.getPubDate()));
        book.setImageUrl(response.getCover() != null ? response.getCover() : "");
        book.setDescription(response.getDescription() != null ? response.getDescription() : "");
        book.setOriginalPrice(toDouble(response.getPriceStandard()));
        book.setSellingPrice(toDouble(
                response.getPriceSales() != null ? response.getPriceSales() : response.getPriceStandard()
        ));
        book.setPageCount(0);
        book.setLikes(0);
        book.setGiftWrappingAvailable(false);

//        for (Category category : categories) {
//            BookCategory bookCategory = new BookCategory();
//            bookCategory.setBook(book);
//            bookCategory.setCategory(category);
//            book.getBookCategories().add(bookCategory);
//        }
        BookCategory bookCategory = new BookCategory();
        bookCategory.setBook(book);
        bookCategory.setCategory(category);
        book.getBookCategories().add(bookCategory);
        return book;
    }

    // 날짜 파싱
    private static LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            return null;
        }
    }

    // Integer → Double 변환
    private static Double toDouble(Integer value) {
        return value != null ? value.doubleValue() : null;
    }
}
