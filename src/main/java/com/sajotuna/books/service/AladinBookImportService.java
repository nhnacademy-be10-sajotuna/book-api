package com.sajotuna.books.service;

import com.sajotuna.books.dto.ItemSearchResponse;
import com.sajotuna.books.exception.ExternalApiException;
import com.sajotuna.books.model.Book;
import com.sajotuna.books.repository.BookRepository;
import com.sajotuna.books.util.AladinConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AladinBookImportService {

    private final RestTemplate restTemplate;
    private final BookListService bookListService;
    private final BookRepository bookRepository;

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
                    .queryParam("SearchTarget", "Book")
                    .queryParam("output", "JS")
                    .queryParam("Version", "20131101")
                    .build(false)
                    .toUriString();

            try {
                ItemSearchResponse response = restTemplate.getForObject(url, ItemSearchResponse.class);
                if (response != null && response.getItem() != null) {
                    List<Book> bookList = AladinConverter.toBookEntityList(response.getItem());

                    List<Book> filteredBooks = bookList.stream()
                            .filter(book -> !bookRepository.existsById(book.getIsbn()))
                            .toList();

                    bookListService.saveAllBooks(filteredBooks); // 전부 저장
                }
            } catch (Exception e) {
                throw new ExternalApiException();
            }
        }
    }

//    public void importBooksByCategoryId(Integer categoryId, int totalPages) {
//         for (int page = 1; page <= totalPages; page++) {
//            String url = UriComponentsBuilder.fromUriString(BASE_URL)
//                    .queryParam("ttbkey", TTB_KEY)
//                    .queryParam("CategoryId", categoryId)
//                    .queryParam("MaxResults", 50)
//                    .queryParam("start", page)
//                    .queryParam("SearchTarget", "Book")
//                    .queryParam("output", "JS")
//                    .queryParam("Version", "20131101")
//                    .toUriString();
//
//            try {
//                ItemSearchResponse response = restTemplate.getForObject(url, ItemSearchResponse.class);
//                if (response != null && response.getItem() != null) {
//                    List<Book> books = AladinConverter.toBookEntityList(response.getItem());
//                    List<Book> filtered = books.stream()
//                            .filter(book -> !bookRepository.existsById(book.getIsbn()))
//                            .toList();
//                    bookListService.saveAllBooks(filtered);
//                    log.info("✅ [카테고리 {}] page {}: {}권 저장 완료", categoryId, page, filtered.size());
//                }
//            } catch (Exception e) {
//                log.error("❌ [카테고리 {}] page {} 실패: {}", categoryId, page, e.getMessage());
//            }
//        }
//    }

}