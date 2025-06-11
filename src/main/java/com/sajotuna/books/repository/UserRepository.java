// src/main/java/com/sajotuna/books/repository/UserRepository.java
package com.sajotuna.books.repository;

import com.sajotuna.books.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // 사용자 이름으로 조회
}