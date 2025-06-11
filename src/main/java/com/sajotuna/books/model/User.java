// src/main/java/com/sajotuna/books/model/User.java
package com.sajotuna.books.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users") // 테이블 이름 명시
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 사용자의 고유 ID

    @Column(nullable = false, unique = true)
    private String username; // 사용자 이름 (로그인 ID 등으로 사용)

    // 사용자가 '좋아요'를 누른 책 목록을 추적하기 위한 관계
    // Book과의 다대다 관계가 Like 엔티티를 통해 관리됩니다.
    // Like 엔티티에서 @ManyToOne으로 User와 Book을 참조하므로,
    // 여기서는 mappedBy를 통해 Like 엔티티의 "user" 필드에 의해 매핑된다고 선언합니다.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Like> likes = new HashSet<>(); // 사용자가 누른 좋아요 목록

    public User(String username) {
        this.username = username;
    }
}