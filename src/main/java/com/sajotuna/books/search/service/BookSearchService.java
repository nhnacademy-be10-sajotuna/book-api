package com.sajotuna.books.search.service;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.sajotuna.books.category.exception.CategoryNotFoundException;
import com.sajotuna.books.category.exception.InvalidCategoryIdFormatException;
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
            default -> {
                sortField = "popularity";
                sortOrder = SortOrder.Desc;
            }
        }

        NativeQuery query;

        if (keyword == null || keyword.isBlank()) {
            //  전체 검색 처리
            query = NativeQuery.builder()
                    .withQuery(q -> q.matchAll(m -> m))
                    .withSort(s -> s.field(f -> f
                            .field(sortField)
                            .order(sortOrder)
                    ))
                    .withPageable(PageRequest.of(page, size))
                    .build();
        } else {
            //  키워드 검색 처리
            query = NativeQuery.builder()
                    .withQuery(q -> q.bool(b -> b
                            .should(s -> s.matchPhrase(mp -> mp// 책 제목에 대응하는 정확한 책 찾기
                                    .field("title")
                                    .query(keyword)
                                    .boost(300f)
                            ))
                            .should(s -> s.multiMatch(mm -> mm
                                    .query(keyword)
                                    .fields(List.of(
                                            "title^100",
                                            "title.synonym^80",
                                            "title.jaso^70",
                                            "description^10",
                                            "tags^50",
                                            "author^30"
                                    ))
                                    .type(TextQueryType.BestFields)
                                    .operator(co.elastic.clients.elasticsearch._types.query_dsl.Operator.And)
                            ))
                    ))
                    .withSort(s -> s.field(f -> f
                            .field(sortField)
                            .order(sortOrder)
                    ))
                    .withPageable(PageRequest.of(page, size))
                    .build();
        }

        SearchHits<BookSearchDocument> hits = operations.search(query, BookSearchDocument.class);

        List<BookSearchDocument> documents = hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();

        List<String> isbns = documents.stream()
                .map(BookSearchDocument::getIsbn)
                        .toList();

        // 여기서 동기화 진행
        bookSearchSynService.updateSearchStats(isbns);

        List<BookSearchResponse> content = hits.getSearchHits().stream()
                .map(hit -> BookSearchResponse.from(hit.getContent()))
                .toList();

        return new PageImpl<>(content, pageable, hits.getTotalHits());
    }

    public Page<BookSearchResponse> searchByCategoryId(
        String category,
        int page,
        int size,
        String sort,
        Pageable pageable
) {
        if (category == null || category.isBlank()) {
            // 카테고리 미지정 시 전체 검색
            return search(null, page, size, sort, pageable);
        }

        Long categoryId;
        try {
            categoryId = Long.parseLong(category);
        } catch (NumberFormatException e) {
            throw new InvalidCategoryIdFormatException(category);
        }

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
            default -> {
                sortField = "popularity";
                sortOrder = SortOrder.Desc;
            }
        }

        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.term(t -> t
                        .field("categoryIds")
                        .value(categoryId)
                ))
                .withSort(s -> s.field(f -> f
                        .field(sortField)
                        .order(sortOrder)
                ))
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<BookSearchDocument> hits = operations.search(query, BookSearchDocument.class);

        List<String> isbns = hits.getSearchHits().stream()
                .map(hit -> hit.getContent().getIsbn())
                .toList();
        bookSearchSynService.updateSearchStats(isbns);

        List<BookSearchResponse> content = hits.getSearchHits().stream()
                .map(hit -> BookSearchResponse.from(hit.getContent()))
                .toList();

        return new PageImpl<>(content, pageable, hits.getTotalHits());
    }

       public List<String> autoCompleteTitle(String keyword) {
           NativeQuery query = NativeQuery.builder()
                   .withQuery(q -> q.matchPhrasePrefix(mpp -> mpp
                           .field("title")
                           .query(keyword)
                   ))
                   .withPageable(PageRequest.of(0, 10))
                   .build();

           SearchHits<BookSearchDocument> hits = operations.search(query, BookSearchDocument.class);
           return hits.getSearchHits().stream()
                   .map(hit -> hit.getContent().getTitle())
                   .distinct()
                   .limit(10)
                   .toList();
       }
}

