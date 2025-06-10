package com.nhnacademy.books.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet; // HashSet 추가
import java.util.List;
import java.util.Set; // Set 추가

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"subCategories", "books"}) // 순환 참조 방지, books 필드도 exclude에 추가
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY) // 상위 카테고리는 지연 로딩
    @JoinColumn(name = "parent_id")
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> subCategories = new ArrayList<>();

    // !!!!!!!! 이 부분을 추가해야 합니다 !!!!!!!!
    @ManyToMany(mappedBy = "categories") // Book 엔티티의 'categories' 필드에 의해 매핑됨을 명시
    private Set<Book> books = new HashSet<>();
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    public Category(String name) {
        this.name = name;
    }

    public Category(String name, Category parentCategory) {
        this.name = name;
        this.parentCategory = parentCategory;
        if (parentCategory != null) {
            parentCategory.addSubCategory(this); // 상위 카테고리에 하위 카테고리 추가
        }
    }

    // 편의 메서드: 상위 카테고리에 하위 카테고리 추가
    public void addSubCategory(Category category) {
        this.subCategories.add(category);
        category.setParentCategory(this);
    }

    // 카테고리 경로를 문자열로 반환
    public String getFullPath() {
        if (parentCategory != null) {
            return parentCategory.getFullPath() + " > " + name;
        }
        return name;
    }
}