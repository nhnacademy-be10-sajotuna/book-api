package com.sajotuna.books.search.repository;

import com.sajotuna.books.search.BookSearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BookSearchRepository extends ElasticsearchRepository<BookSearchDocument, String> {
}
