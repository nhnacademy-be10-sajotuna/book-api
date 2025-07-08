package com.sajotuna.books.book.service;


import com.sajotuna.books.book.controller.response.AladinBookResponse;
import com.sajotuna.books.book.domain.Book;
import com.sajotuna.books.category.domain.Category;
import com.sajotuna.books.book.repository.BookRepository;
import com.sajotuna.books.common.util.AladinConverter;
import com.sajotuna.books.category.service.CategoryService;
import com.sajotuna.books.search.BookSearchDocument;
import com.sajotuna.books.search.repository.BookSearchRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AladinBookImportService {

    private final BookListService bookListService;
    private final BookRepository bookRepository;
    private final CategoryService categoryService;
    private final BookSearchRepository bookSearchRepository;
    private final AladinStockService aladinStockService;

    @Transactional
    public void importBooks(List<AladinBookResponse> responses) {
        List<Book> books = responses.stream()
                .map(item -> {
                    List<Category> categories = categoryService.findOrCreateCategories(item.getCategoryNames());
                    return AladinConverter.toBookEntity(item, List.of(categories.getLast()));
                })
                .filter(book -> !bookRepository.existsById(book.getIsbn()) && !book.getIsbn().isBlank())
                .toList();

        bookListService.saveAllBooks(books); // RDB 저장

        bookSearchRepository.saveAll(
                books.stream()
//                                .filter(book -> book.getIsbn() != null && !book.getIsbn().isBlank())
                    .map(BookSearchDocument::from)
                    .toList()
        );

        aladinStockService.syncStockWithOrderApi(responses);
    }
}