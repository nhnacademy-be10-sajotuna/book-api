package com.sajotuna.books.service.impl;

import com.sajotuna.books.dto.BookResponse;
import com.sajotuna.books.dto.SearchRequest;
import com.sajotuna.books.exception.InvalidSortCriteriaException;
import com.sajotuna.books.model.Book;
import com.sajotuna.books.model.BookTag; // BookTag 임포트 추가
import com.sajotuna.books.model.Tag;
import com.sajotuna.books.repository.BookRepository;
import com.sajotuna.books.repository.BookTagRepository; // BookTagRepository 임포트 추가
import com.sajotuna.books.service.SearchService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchServiceImpl implements SearchService {

    private final BookRepository bookRepository;
    private final BookTagRepository bookTagRepository; // 의존성 주입 (사용하지 않더라도 에러 해결을 위해 포함)

    // 동의어/유의어 맵 초기화 (실제 운영에서는 DB 또는 외부 설정 파일에서 관리)
    private static final Map<String, Set<String>> SYNONYM_MAP = new HashMap<>();

    static {
        SYNONYM_MAP.put("아기", new HashSet<>(Arrays.asList("유아")));
        SYNONYM_MAP.put("학생", new HashSet<>(Arrays.asList("제자")));
        SYNONYM_MAP.put("구입", new HashSet<>(Arrays.asList("구매")));
        SYNONYM_MAP.put("예쁜", new HashSet<>(Arrays.asList("아름다운")));
        SYNONYM_MAP.put("슬픈", new HashSet<>(Arrays.asList("우울한")));
        SYNONYM_MAP.put("기질", new HashSet<>(Arrays.asList("특성")));
        SYNONYM_MAP.put("la", new HashSet<>(Arrays.asList("로스앤젤레스")));
        SYNONYM_MAP.put("로스앤젤레스", new HashSet<>(Arrays.asList("la"))); // 양방향으로 추가
        // 기타 필요한 동의어 추가
    }

    @Override
    public List<BookResponse> searchBooks(SearchRequest request) {
        String keyword = request.getKeyword();
        String sortBy = request.getSortBy();

        // N+1 문제를 방지하기 위해 Book, Category, BookTag, Tag를 함께 가져옵니다.
        List<Book> allBooks = bookRepository.findAllWithCategoriesAndTags();

        // 1. 동의어/유의어 처리 및 검색 키워드 확장
        Set<String> searchKeywords = new HashSet<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerCaseKeyword = keyword.toLowerCase();
            searchKeywords.add(lowerCaseKeyword);
            // 동의어 추가
            if (SYNONYM_MAP.containsKey(lowerCaseKeyword)) {
                SYNONYM_MAP.get(lowerCaseKeyword).forEach(synonym -> searchKeywords.add(synonym.toLowerCase()));
            }
        }

        // 2. 가중치 기반 검색 및 필터링
        Map<Book, Double> bookScores = new HashMap<>();
        for (Book book : allBooks) {
            double score = 0.0;

            // 도서 명 (title) 가중치: 100
            for (String k : searchKeywords) {
                if (book.getTitle() != null && book.getTitle().toLowerCase().contains(k)) {
                    score += 100.0;
                }
            }

            // 도서 설명 (description) 가중치: 10
            for (String k : searchKeywords) {
                if (book.getDescription() != null && book.getDescription().toLowerCase().contains(k)) {
                    score += 10.0;
                }
            }

            // 저자 (author) 가중치: 80 (예시)
            for (String k : searchKeywords) {
                if (book.getAuthor() != null && book.getAuthor().toLowerCase().contains(k)) {
                    score += 80.0;
                }
            }

            // 태그 (Tag) 가중치: 50
            for (String k : searchKeywords) {
                boolean tagMatch = book.getBookTags().stream()
                        .map(bookTag -> bookTag.getTag().getTagName().toLowerCase()) // BookTag::getTag().getTagName() 사용
                        .anyMatch(tagName -> tagName.contains(k));
                if (tagMatch) {
                    score += 50.0;
                }
            }

            // 검색 키워드가 없거나, 점수가 0보다 커야 결과에 포함 (키워드가 없으면 모든 책 반환)
            if (searchKeywords.isEmpty() || score > 0) {
                bookScores.put(book, score);
            }
        }

        // 점수가 0인 책 필터링 (키워드가 있고, 매칭되는 책이 없는 경우)
        List<Book> filteredBooks = bookScores.entrySet().stream()
                .filter(entry -> searchKeywords.isEmpty() || entry.getValue() > 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());


        // 3. 정렬 기준 적용
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            switch (sortBy.toLowerCase()) {
                case "popularity": // 인기도: 좋아요 수 + 조회수 (예시)
                    filteredBooks.sort(Comparator.<Book>comparingInt(b -> b.getLikes() != null ? b.getLikes() : 0)
                            .thenComparingInt(b -> b.getViewCount() != null ? b.getViewCount() : 0)
                            .reversed());
                    break;
                case "new_arrivals": // 신상품: 발행일 기준 내림차순
                    filteredBooks.sort(Comparator.comparing(Book::getPublicationDate, Comparator.nullsLast(Comparator.reverseOrder())));
                    break;
                case "lowest_price": // 최저가: 판매가 기준 오름차순
                    filteredBooks.sort(Comparator.comparing(Book::getSellingPrice, Comparator.nullsLast(Comparator.naturalOrder())));
                    break;
                case "highest_price": // 최고가: 판매가 기준 내림차순
                    filteredBooks.sort(Comparator.comparing(Book::getSellingPrice, Comparator.nullsLast(Comparator.reverseOrder())));
                    break;
                case "rating": // 평점: 최소 100건 이상 평점평균 순으로 내림차순 정렬 (리뷰 수 100 미만인 책은 제외)
                    // `Comparator.<Book>`을 명시적으로 추가하여 컴파일러에게 타입을 알려줍니다.
                    filteredBooks.sort(Comparator.<Book>comparingDouble(b -> {
                                if (b.getReviewCount() != null && b.getReviewCount() >= 100) {
                                    return b.getAverageRating();
                                }
                                return 0.0; // 100건 미만은 0점으로 처리 (정렬에서 뒤로 밀리게)
                            }).reversed()
                            .thenComparingLong(b -> b.getReviewCount() != null ? b.getReviewCount() : 0L) // 평점 같으면 리뷰 수 많은 순
                            .reversed());
                    break;
                case "reviews": // 리뷰: 리뷰 수를 기준으로 내림차순 정렬
                    // `Comparator.<Book>`을 명시적으로 추가하여 컴파일러에게 타입을 알려줍니다.
                    filteredBooks.sort(Comparator.<Book>comparingLong(b -> b.getReviewCount() != null ? b.getReviewCount() : 0L).reversed());
                    break;
                default:
                    throw new InvalidSortCriteriaException(sortBy);
            }
        } else {
            // 정렬 기준이 없으면 가중치(score)를 기준으로 내림차순 정렬
            // `Comparator.<Book>`을 명시적으로 추가하여 컴파일러에게 타입을 알려줍니다.
            filteredBooks.sort(Comparator.<Book>comparingDouble(bookScores::get).reversed());
        }

        return filteredBooks.stream()
                .map(BookResponse::new)
                .collect(Collectors.toList());
    }
}