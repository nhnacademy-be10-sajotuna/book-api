package com.sajotuna.books.search.service;

import com.sajotuna.books.search.BookSearchDocument;
import com.sajotuna.books.search.repository.BookSearchRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookStatsService {

    private final BookSearchRepository bookSearchRepository;
    
    // 메모리 버퍼 - 통계 업데이트 대기 중인 데이터
    private final Map<String, StatsDelta> pendingUpdates = new ConcurrentHashMap<>();
    
    /**
     * 조회수 증가 + 실시간 인기도 재계산
     */
    public void incrementViewCount(String isbn) {
        pendingUpdates.computeIfAbsent(isbn, k -> new StatsDelta())
                .incrementViewCount();
        
        // 실시간 인기도 재계산
        recalculatePopularityAsync(isbn);
        
        log.debug("View count queued for ISBN: {}", isbn);
    }
    
    /**
     * 검색횟수 증가 + 실시간 인기도 재계산
     */
    public void incrementSearchCount(String isbn) {
        pendingUpdates.computeIfAbsent(isbn, k -> new StatsDelta())
                .incrementSearchCount();
        
        // 실시간 인기도 재계산
        recalculatePopularityAsync(isbn);
        
        log.debug("Search count queued for ISBN: {}", isbn);
    }
    
    /**
     * 검색횟수 배치 증가
     */
    public void incrementSearchCounts(List<String> isbns) {
        isbns.forEach(this::incrementSearchCount);
    }
    
    /**
     * 리뷰 통계 업데이트 (평균평점, 리뷰수) - 인기도 재계산 없음
     */
    public void updateReviewStats(String isbn, double newAverageRating, int newReviewCount) {
        pendingUpdates.computeIfAbsent(isbn, k -> new StatsDelta())
                .updateReviewStats(newAverageRating, newReviewCount);
        
        log.debug("Review stats queued for ISBN: {}", isbn);
    }
    
    /**
     * 실시간 인기도 재계산
     * 공식: 조회수 * 0.7 + 검색횟수 * 0.3
     */
    private void recalculatePopularityAsync(String isbn) {
        try {
            // ES에서 현재 통계 조회
            BookSearchDocument book = bookSearchRepository.findById(isbn).orElse(null);
            if (book == null) {
                log.warn("Book not found in ES for popularity calculation: {}", isbn);
                return;
            }
            
            // 메모리 버퍼의 변경사항 적용
            StatsDelta delta = pendingUpdates.get(isbn);
            long currentViewCount = book.getViewCount();
            long currentSearchCount = book.getSearchCount();
            
            if (delta != null) {
                currentViewCount += delta.getViewCountDelta();
                currentSearchCount += delta.getSearchCountDelta();
            }
            
            // 인기도 계산
            double newPopularity = calculatePopularity(currentViewCount, currentSearchCount);
            
            // 인기도 업데이트 큐에 추가
            pendingUpdates.computeIfAbsent(isbn, k -> new StatsDelta())
                    .updatePopularity(newPopularity);
            
            log.debug("Popularity recalculated for ISBN {}: {}", isbn, newPopularity);
            
        } catch (Exception e) {
            log.error("Failed to recalculate popularity for ISBN {}: {}", isbn, e.getMessage());
        }
    }
    
    /**
     * 인기도 계산 공식
     * 조회수 * 0.7 + 검색횟수 * 0.3
     */
    private double calculatePopularity(long viewCount, long searchCount) {
        return (viewCount * 0.7) + (searchCount * 0.3);
    }
    
    /**
     * 30초마다 ES에 Bulk 업데이트
     */
    @Scheduled(fixedDelay = 30000)
    public void flushPendingUpdates() {
        if (pendingUpdates.isEmpty()) {
            return;
        }
        
        log.info("Flushing {} pending stats updates to Elasticsearch", pendingUpdates.size());
        
        try {
            // ES Bulk 업데이트 수행
            bulkUpdateStats();
            
            // 성공 시 버퍼 클리어
            pendingUpdates.clear();
            
        } catch (Exception e) {
            log.error("Failed to flush stats updates: {}", e.getMessage());
            // 실패 시 버퍼 유지하여 다음 스케줄에서 재시도
        }
    }
    
    /**
     * ES Bulk 업데이트 수행
     */
    private void bulkUpdateStats() {
        log.info("Bulk updating {} documents", pendingUpdates.size());
        
        try {
            // 1. 업데이트할 문서들을 조회
            List<String> isbns = pendingUpdates.keySet().stream().toList();
            Iterable<BookSearchDocument> documents = bookSearchRepository.findAllById(isbns);
            
            // 2. 문서들을 업데이트
            List<BookSearchDocument> updatedDocuments = new ArrayList<>();
            
            for (BookSearchDocument doc : documents) {
                StatsDelta delta = pendingUpdates.get(doc.getIsbn());
                if (delta != null) {
                    // 통계 업데이트
                    if (delta.getViewCountDelta() > 0) {
                        doc.setViewCount(doc.getViewCount() + delta.getViewCountDelta());
                    }
                    if (delta.getSearchCountDelta() > 0) {
                        doc.setSearchCount(doc.getSearchCount() + delta.getSearchCountDelta());
                    }
                    if (delta.getNewReviewCount() != null) {
                        doc.setReviewCount(delta.getNewReviewCount());
                    }
                    if (delta.getNewAverageRating() != null) {
                        doc.setAverageRating(delta.getNewAverageRating());
                    }
                    if (delta.getNewPopularity() != null) {
                        doc.setPopularity(delta.getNewPopularity());
                    }
                    
                    updatedDocuments.add(doc);
                }
            }
            
            // 3. Bulk 업데이트 실행
            if (!updatedDocuments.isEmpty()) {
                bookSearchRepository.saveAll(updatedDocuments);
                log.info("Successfully bulk updated {} documents", updatedDocuments.size());
            }
            
        } catch (Exception e) {
            log.error("Failed to perform bulk update: {}", e.getMessage());
            throw e;
        }
    }
    
    
    /**
     * 통계 변화량을 저장하는 내부 클래스
     */
    @Getter
    public static class StatsDelta {
        // Getters
        private int viewCountDelta = 0;
        private int searchCountDelta = 0;
        private Integer newReviewCount = null;
        private Double newAverageRating = null;
        private Double newPopularity = null;
        
        public void incrementViewCount() {
            this.viewCountDelta++;
        }
        
        public void incrementSearchCount() {
            this.searchCountDelta++;
        }
        
        public void updateReviewStats(double newAverageRating, int newReviewCount) {
            this.newReviewCount = newReviewCount;
            this.newAverageRating = newAverageRating;
        }
        
        public void updatePopularity(double newPopularity) {
            this.newPopularity = newPopularity;
        }

    }
}