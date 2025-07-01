package com.sajotuna.books.like.repository;

import com.sajotuna.books.book.domain.Book; // Book 임포트
import com.sajotuna.books.like.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    // 특정 사용자가 특정 책에 '좋아요'를 눌렀는지 확인
    Optional<Like> findByUserIdAndBookIsbn(Long userId, String bookIsbn);

    // 특정 사용자가 '좋아요'를 누른 모든 책 조회
    List<Like> findByUserId(Long userId);

    // 특정 책과 관련된 모든 좋아요 삭제 (도서 삭제 시 사용)
    void deleteByBook(Book book);
}