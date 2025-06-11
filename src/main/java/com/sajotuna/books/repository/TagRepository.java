package com.sajotuna.books.repository;


import com.sajotuna.books.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByTagName(String tagName);
    boolean existsByTagName(String tagName);
}

