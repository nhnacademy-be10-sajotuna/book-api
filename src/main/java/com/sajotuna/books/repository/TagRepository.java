// src/main/java/com/sajotuna/books/repository/TagRepository.java
package com.sajotuna.books.repository;

import com.sajotuna.books.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    // 이름으로 태그를 찾는 메서드 추가
    Optional<Tag> findByName(String name);
}