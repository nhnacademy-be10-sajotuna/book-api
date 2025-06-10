// src/main/java/com/sajotuna/books/service/LikeService.java
package com.sajotuna.books.service;

import com.sajotuna.books.dto.BookResponse; // 좋아요한 책 목록을 BookResponse로 반환
import com.sajotuna.books.dto.LikeRequest;
import com.sajotuna.books.dto.LikeResponse;

import java.util.List;

public interface LikeService {
    LikeResponse addLike(LikeRequest likeRequest); // 좋아요 추가
    void removeLike(Long userId, String bookIsbn); // 좋아요 취소
    List<BookResponse> getLikedBooksByUserId(Long userId); // 특정 사용자가 좋아요한 책 목록 조회
    boolean isLiked(Long userId, String bookIsbn); // 특정 사용자가 특정 책에 좋아요를 눌렀는지 확인
}