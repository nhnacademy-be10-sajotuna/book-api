package com.sajotuna.books.search.service;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.sajotuna.books.search.BookSearchDocument;
import com.sajotuna.books.search.controller.reponse.BookSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookSearchService {

    private final ElasticsearchOperations operations;
    private final BookSearchSynService bookSearchSynService;

    public Page<BookSearchResponse> search(String keyword, int page, int size, String sort, Pageable pageable) {

        String sortField;
        SortOrder sortOrder;

        switch (sort) {
            case "newest" -> {
                sortField = "publishedDate";
                sortOrder = SortOrder.Desc;
            }
            case "lowestPrice" -> {
                sortField = "sellingPrice";
                sortOrder = SortOrder.Asc;
            }
            case "highestPrice" -> {
                sortField = "sellingPrice";
                sortOrder = SortOrder.Desc;
            }
            case "rating" -> {
                sortField = "averageRating";
                sortOrder = SortOrder.Desc;
            }
            case "review" -> {
                sortField = "reviewCount";
                sortOrder = SortOrder.Desc;
            }
            case "popularity" -> {
                sortField = "popularity";
                sortOrder = SortOrder.Desc;
            }
            default -> {
                sortField = "popularity";
                sortOrder = SortOrder.Desc;
            }
        }

        NativeQuery query = NativeQuery.builder()
                .withTrackScores(false)
                .withQuery(q -> q.bool(b -> b
                        .must(m -> m.multiMatch(mm -> mm
                                .query(keyword)
                                .fields(
                                        "title^100",
                                        "title.synonym^80",
                                        "title.jaso^70",
                                        "description^10",
                                        "tags^50",
                                        "author^30"
                                )
                                .type(TextQueryType.BestFields)
                        ))
                ))

                .withSort(s -> s.field(f -> f
                        .field(sortField)
                        .order(sortOrder)
                ))
                .withPageable(PageRequest.of(page, size))
                .build();



        SearchHits<BookSearchDocument> hits = operations.search(query, BookSearchDocument.class);

        List<BookSearchDocument> documents = hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();

        List<String> isbns = documents.stream()
                .map(BookSearchDocument::getIsbn)
                        .toList();

        // 여기서 동기화 진행
        bookSearchSynService.updateSearchStats(isbns);

        List<BookSearchResponse> content = documents.stream()
                .map(BookSearchResponse::from)
                .toList();

        return new PageImpl<>(content, pageable, hits.getTotalHits());
    }
}
