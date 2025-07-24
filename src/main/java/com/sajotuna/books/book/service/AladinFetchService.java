package com.sajotuna.books.book.service;

import com.sajotuna.books.book.controller.response.AladinBookResponse;
import com.sajotuna.books.book.controller.response.ItemSearchResponse;
import com.sajotuna.books.book.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AladinFetchService {

    private final RestTemplate restTemplate;

    @Value("${aladin.url}")
    private String BASE_URL;

    @Value("${aladin.key}")
    private String TTB_KEY;

    public List<AladinBookResponse> fetchBooks(String keyword, int totalPages) {
        List<AladinBookResponse> allBooks = new ArrayList<>();

        for (int page = 1; page <= totalPages; page++) {
            String url = UriComponentsBuilder.fromUriString(BASE_URL)
                    .queryParam("ttbkey", TTB_KEY)
                    .queryParam("Query", keyword)
                    .queryParam("QueryType", "Keyword")
                    .queryParam("MaxResults", 50)
                    .queryParam("start", page)
                    .queryParam("SearchTarget", "All")
                    .queryParam("output", "JS")
                    .queryParam("Version", "20131101")
                    .queryParam("Cover", "Big")
                    .build(false)
                    .toUriString();

            log.info("import url: {}", url);
            try {
                ItemSearchResponse response = restTemplate.getForObject(url, ItemSearchResponse.class);
                if (response != null && response.getItem() != null) {
                    allBooks.addAll(response.getItem());
                }
            } catch (Exception e) {
                throw new ExternalApiException();
            }
        }

        return allBooks;
    }
}
