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

    private boolean isChosung(String keyword) {
        return keyword != null && keyword.matches("^[ㄱ-ㅎ]+$");
    }

    public Page<BookSearchResponse> search(String keyword, String category, int page, int size, String sort, Pageable pageable) {
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

        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasCategory = category != null && !category.isBlank();

        NativeQuery query;

        if (!hasKeyword && !hasCategory) {
            // 전체 검색
            query = NativeQuery.builder()
                    .withQuery(q -> q.matchAll(m -> m))
                    .withSort(s -> s.field(f -> f.field(sortField).order(sortOrder)))
                    .withPageable(PageRequest.of(page, size))
                    .build();
        } else {
            // 키워드 or 카테고리 조건 포함 검색
            query = NativeQuery.builder()
                    .withQuery(q -> q.bool(b -> {
                        // keyword 처리
                        if (hasKeyword) {
                            if (isChosung(keyword)) {
                                b.must(m -> m.matchPhrasePrefix(mp -> mp
                                        .field("titleChosung")
                                        .query(keyword)
                                        .boost(300f)
                                ));
                            } else {
                                b.must(m -> m.bool(bb -> bb
                                        .should(s -> s.matchPhrase(mp -> mp
                                                .field("title")
                                                .query(keyword)
                                                .boost(300f)))
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
                                ));
                            }
                        }

                        // category 처리
                        if (hasCategory) {
                            try {
                                Long categoryId = Long.parseLong(category);
                                b.must(m -> m.term(t -> t
                                        .field("categoryIds")
                                        .value(categoryId)
                                ));
                            } catch (NumberFormatException e) {
                                throw new InvalidCategoryIdFormatException(category);
                            }
                        }

                        return b;
                    }))
                    .withSort(s -> s.field(f -> f.field(sortField).order(sortOrder)))
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

        bookSearchSynService.updateSearchStats(isbns);

        List<BookSearchResponse> content = documents.stream()
                .map(BookSearchResponse::from)
                .toList();

        return new PageImpl<>(content, pageable, hits.getTotalHits());
    }


//    public Page<BookSearchResponse> searchByCategoryId(
//        String category,
//        int page,
//        int size,
//        String sort,
//        Pageable pageable
//) {
//        if (category == null || category.isBlank()) {
//            // 카테고리 미지정 시 전체 검색
//            return search(null, page, size, sort, pageable);
//        }
//
//        Long categoryId;
//        try {
//            categoryId = Long.parseLong(category);
//        } catch (NumberFormatException e) {
//            throw new InvalidCategoryIdFormatException(category);
//        }
//
//        String sortField;
//        SortOrder sortOrder;
//
//        switch (sort) {
//            case "newest" -> {
//                sortField = "publishedDate";
//                sortOrder = SortOrder.Desc;
//            }
//            case "lowestPrice" -> {
//                sortField = "sellingPrice";
//                sortOrder = SortOrder.Asc;
//            }
//            case "highestPrice" -> {
//                sortField = "sellingPrice";
//                sortOrder = SortOrder.Desc;
//            }
//            case "rating" -> {
//                sortField = "averageRating";
//                sortOrder = SortOrder.Desc;
//            }
//            case "review" -> {
//                sortField = "reviewCount";
//                sortOrder = SortOrder.Desc;
//            }
//            default -> {
//                sortField = "popularity";
//                sortOrder = SortOrder.Desc;
//            }
//        }
//
//        NativeQuery query = NativeQuery.builder()
//                .withQuery(q -> q.term(t -> t
//                        .field("categoryIds")
//                        .value(categoryId)
//                ))
//                .withSort(s -> s.field(f -> f
//                        .field(sortField)
//                        .order(sortOrder)
//                ))
//                .withPageable(PageRequest.of(page, size))
//                .build();
//
//        SearchHits<BookSearchDocument> hits = operations.search(query, BookSearchDocument.class);
//
//        List<String> isbns = hits.getSearchHits().stream()
//                .map(hit -> hit.getContent().getIsbn())
//                .toList();
//        bookSearchSynService.updateSearchStats(isbns);
//
//        List<BookSearchResponse> content = hits.getSearchHits().stream()
//                .map(hit -> BookSearchResponse.from(hit.getContent()))
//                .toList();
//
//        return new PageImpl<>(content, pageable, hits.getTotalHits());
//    }

    public List<String> autoCompleteTitle(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        NativeQuery query;

        if (isChosung(keyword)) {
            query = NativeQuery.builder()
                    .withQuery(q -> q.matchPhrasePrefix(mpp -> mpp
                            .field("titleChosung")
                            .query(keyword)
                    ))
                    .withPageable(PageRequest.of(0, 10))
                    .build();
        } else {
            query = NativeQuery.builder()
                    .withQuery(q -> q.matchPhrasePrefix(mpp -> mpp
                            .field("titleAutocomplete")
                            .query(keyword)
                    ))
                    .withPageable(PageRequest.of(0, 10))
                    .build();
        }

        SearchHits<BookSearchDocument> hits = operations.search(query, BookSearchDocument.class);
        return hits.getSearchHits().stream()
                .map(hit -> hit.getContent().getTitle())
                .distinct()
                .limit(10)
                .toList();
    }

}

