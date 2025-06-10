package com.sajotuna.books.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "likes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(LikeId.class)
public class Like {

    @Id
    @Column(name = "isbn", length = 20)
    private String isbn;

    @Id
    @Column(name = "member_id")
    private Integer memberId;

    // Book 엔티티가 프로젝트 내에 있다면 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isbn", referencedColumnName = "isbn", insertable = false, updatable = false)
    private Book book;

    // Member 엔티티는 외부 시스템에 있으므로 직접 참조하지 않음
}
