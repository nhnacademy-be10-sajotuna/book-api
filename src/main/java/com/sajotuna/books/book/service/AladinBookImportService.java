package com.sajotuna.books.book.service;

import com.sajotuna.books.book.controller.response.ItemSearchResponse;
import com.sajotuna.books.book.exception.ExternalApiException;
import com.sajotuna.books.book.domain.Book;
import com.sajotuna.books.category.domain.Category;
import com.sajotuna.books.book.repository.BookRepository;
import com.sajotuna.books.common.util.AladinConverter;
import com.sajotuna.books.category.service.CategoryService;
import com.sajotuna.books.search.BookSearchDocument;
import com.sajotuna.books.search.repository.BookSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AladinBookImportService {

    private final RestTemplate restTemplate;
    private final BookListService bookListService;
    private final BookRepository bookRepository;
    private final CategoryService categoryService;
    private final BookSearchRepository bookSearchRepository;

    private final String BASE_URL = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx";
    private final String TTB_KEY = "ttbdlguswn82541342001";

    public void importBooksByKeyword(String keyword, int totalPages) {
        for(int page = 1; page <= totalPages; page++) {
            String url = UriComponentsBuilder.fromUriString(BASE_URL)
                    .queryParam("ttbkey", TTB_KEY)
                    .queryParam("Query", keyword)
                    .queryParam("QueryType", "Keyword")
                    .queryParam("MaxResults", 50)
                    .queryParam("start", page)
                    .queryParam("SearchTarget", "All")
                    .queryParam("output", "JS")
                    .queryParam("Version", "20131101")
                    .build(false)
                    .toUriString();

            try {
                ItemSearchResponse response = restTemplate.getForObject(url, ItemSearchResponse.class);
                if (response != null && response.getItem() != null) {

                    List<Book> books = response.getItem().stream()
                            .map(item -> {
                                List<Category> categories = categoryService.findOrCreateCategories(item.getCategoryNames());
                                Category last = null;
                                if (!categories.isEmpty()) {
                                    last = categories.getLast();
                                }
                                return AladinConverter.toBookEntity(item, last);
                            })
                            .filter(book -> !bookRepository.existsById(book.getIsbn()))
                            .toList();

                    bookListService.saveAllBooks(books); // RDB 저장

//                   books.forEach(book ->
//                            bookSearchRepository.save(BookSearchDocument.from(book)));

//                    books.stream()
//                            .filter(book -> book.getIsbn() == null || book.getIsbn().isBlank())
//                            .forEach(book -> System.out.println("❗ Invalid ISBN: " + book.getTitle()));

                    bookSearchRepository.saveAll(
                            books.stream()
                                    .filter(book -> book.getIsbn() != null && !book.getIsbn().isBlank())
                                    .map(BookSearchDocument::from)
                                    .toList()
                    );


                }
            } catch (Exception e) {
                log.error("Error while importing books", e);
                throw new ExternalApiException();
            }
        }
    }


}