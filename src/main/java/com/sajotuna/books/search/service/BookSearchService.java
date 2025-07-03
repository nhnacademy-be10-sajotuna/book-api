package com.sajotuna.books.search.service;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query; // Query 임포트 추가
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.sajotuna.books.category.domain.Category;
import com.sajotuna.books.category.exception.CategoryNotFoundException;
import com.sajotuna.books.category.repository.CategoryRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookSearchService {

    private final ElasticsearchOperations operations;
    private final BookSearchSynService bookSearchSynService;
    private final CategoryRepository categoryRepository;

    public Page<BookSearchResponse> search(String keyword, int page, int size, String sort, Long categoryId, Pageable pageable) {

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

        // BoolQuery Builder를 사용하여 must와 filter 조건을 동적으로 추가
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder()
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
                ));

        // 카테고리 ID가 제공되면 필터 조건 추가
        if (categoryId != null) {
            // categoryId로 Category 엔티티를 찾아 해당 카테고리 이름을 가져옵니다.
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundException(categoryId));

            // BookSearchDocument의 categories 필드에 저장되는 방식과 일치하도록
            // 해당 카테고리의 이름으로 필터링합니다.
            boolQueryBuilder.filter(f -> f.term(t -> t.field("categories").value(category.getName())));
        }

        NativeQuery query = NativeQuery.builder()
                .withTrackScores(false)
                // 오류 해결: 이미 빌드된 BoolQuery 객체를 Query.of()로 감싸서 withQuery에 전달합니다.
                .withQuery(Query.of(q -> q.bool(boolQueryBuilder.build())))
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

        List<BookSearchResponse> content = hits.getSearchHits().stream()
                .map(hit -> BookSearchResponse.from(hit.getContent()))
                .toList();

        return new PageImpl<>(content, pageable, hits.getTotalHits());
    }
}