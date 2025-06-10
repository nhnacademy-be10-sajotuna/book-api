package com.sajotuna.books.model;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeId implements Serializable {
    private String isbn;
    private Integer memberId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LikeId that)) return false;
        return isbn.equals(that.isbn) && memberId.equals(that.memberId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(isbn, memberId);
    }
}
