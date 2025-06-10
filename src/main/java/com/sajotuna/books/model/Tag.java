// src/main/java/com/sajotuna/books/model/Tag.java
package com.sajotuna.books.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity // JPA 엔티티로 선언
@Getter // Lombok: 모든 필드에 대한 getter 메서드 자동 생성
@Setter // Lombok: 모든 필드에 대한 setter 메서드 자동 생성
@NoArgsConstructor // Lombok: 기본 생성자 자동 생성
@Table(name = "tags") // 이 엔티티가 매핑될 데이터베이스 테이블 이름 지정
public class Tag {

    @Id // 기본 키(Primary Key)로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 데이터베이스에서 ID 자동 생성 전략 (AUTO_INCREMENT)
    private Long id; // 태그의 고유 ID

    @Column(nullable = false, unique = true) // 데이터베이스 컬럼으로 매핑, null 불가능, 고유 값
    private String name; // 태그 이름 (예: "자바", "판타지")

    // 생성자 (필요에 따라 추가)
    public Tag(String name) {
        this.name = name;
    }

    // ID와 이름을 모두 받는 생성자 (선택 사항)
    public Tag(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}