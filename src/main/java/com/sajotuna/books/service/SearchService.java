package com.sajotuna.books.service;

import com.sajotuna.books.dto.BookResponse;
import com.sajotuna.books.dto.SearchRequest;

import java.util.List;

public interface SearchService {
    List<BookResponse> searchBooks(SearchRequest request);
}