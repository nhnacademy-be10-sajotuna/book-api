package com.nhnacademy.books.model;

import com.fasterxml.jackson.annotation.JsonBackReference; // 추가
import com.fasterxml.jackson.annotation.JsonManagedReference; // 추가
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"subCategories", "books"})
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonBackReference // !!!!!!!! 이 부분 추가 !!!!!!!!
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // !!!!!!!! 이 부분 추가 !!!!!!!!
    private List<Category> subCategories = new ArrayList<>();

    @ManyToMany(mappedBy = "categories")
    @JsonBackReference // !!!!!!!! 이 부분 추가 (Book의 categories 필드에 대한 역참조) !!!!!!!!
    private Set<Book> books = new HashSet<>();

    public Category(String name) {
        this.name = name;
    }

    public Category(String name, Category parentCategory) {
        this.name = name;
        this.parentCategory = parentCategory;
        if (parentCategory != null) {
            parentCategory.addSubCategory(this);
        }
    }

    public void addSubCategory(Category category) {
        this.subCategories.add(category);
        category.setParentCategory(this);
    }

    public String getFullPath() {
        if (parentCategory != null) {
            return parentCategory.getFullPath() + " > " + name;
        }
        return name;
    }
}