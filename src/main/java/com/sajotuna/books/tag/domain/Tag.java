package com.sajotuna.books.tag.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // 태그ID
    private Long id;

    @Column(name = "tag_name")
    private String tagName;

    // orphanRemoval = true 추가: Tag 삭제 시 연결된 BookTag도 함께 삭제
    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookTag> bookTags = new HashSet<>();

    public Tag(String tagName) {
        this.tagName = tagName;
    }
}