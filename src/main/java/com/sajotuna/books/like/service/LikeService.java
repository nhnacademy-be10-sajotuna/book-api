// src/main/java/com/sajotuna/books/service/LikeService.java
package com.sajotuna.books.like.service;

import com.sajotuna.books.book.controller.response.BookResponse; // 좋아요한 책 목록을 BookResponse로 반환
import com.sajotuna.books.like.controller.request.LikeRequest;
import com.sajotuna.books.like.controller.response.LikeResponse;

import java.util.List;

public interface LikeService {
    LikeResponse addLike(Long userId, LikeRequest likeRequest); // 좋아요 추가
    void removeLike(Long userId, String bookIsbn); // 좋아요 취소
    List<BookResponse> getLikedBooksByUserId(Long userId); // 특정 사용자가 좋아요한 책 목록 조회
    boolean isLiked(Long userId, String bookIsbn); // 특정 사용자가 특정 책에 좋아요를 눌렀는지 확인
}