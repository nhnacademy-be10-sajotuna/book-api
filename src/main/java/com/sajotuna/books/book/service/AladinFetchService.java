package com.sajotuna.books.book.service;

import com.sajotuna.books.book.controller.request.AladinStockRequest;
import com.sajotuna.books.book.controller.response.AladinBookResponse;
import com.sajotuna.books.book.controller.response.ItemSearchResponse;
import com.sajotuna.books.book.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AladinFetchService {

    private final RestTemplate restTemplate;

    private final String BASE_URL = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx";
    private final String TTB_KEY = "ttbdlguswn82541342001";

    public List<AladinBookResponse> fetchBooks(String keyword, int totalPages) {
        List<AladinBookResponse> allBooks = new ArrayList<>();

        for (int page = 1; page <= totalPages; page++) {
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
                    allBooks.addAll(response.getItem());
                }
            } catch (Exception e) {
                throw new ExternalApiException();
            }
        }

        return allBooks;
    }
}
