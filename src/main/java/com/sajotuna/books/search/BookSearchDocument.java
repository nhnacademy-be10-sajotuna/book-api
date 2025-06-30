package com.sajotuna.books.search;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sajotuna.books.book.domain.Book;
import org.springframework.data.annotation.Id;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "book4")
@Setting(settingPath = "/elasticsearch/settings.json")
@Mapping(mappingPath = "/elasticsearch/mappings.json")
public class BookSearchDocument {

    @Id
    private String id;

    private String isbn;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "korean_icu_analyzer"),
            otherFields = {
                    @InnerField(suffix = "jaso", type = FieldType.Text, analyzer = "jaso_analyzer"),
                    @InnerField(suffix = "synonym", type = FieldType.Text, analyzer = "synonym_analyzer")
            }
    )
    private String title;

    private String description;

    @Field(type = FieldType.Text, analyzer = "korean_icu_analyzer")
    private String author;

    private Set<String> tags;

    @Field(type = FieldType.Date)
    private LocalDate publishedDate;

    @Field(type = FieldType.Double)
    private Double sellingPrice;

    @Field(type = FieldType.Double)
    private Double averageRating;

    @Field(type = FieldType.Integer)
    private int reviewCount;

    @Field(type = FieldType.Integer)
    private int viewCount;

    @Field(type = FieldType.Integer)
    private Integer searchCount;

    @Field(type = FieldType.Text)
    private String imageUrl;

    @Field(type = FieldType.Double)
    private Double popularity;


    public static BookSearchDocument from(Book book) {
        return BookSearchDocument.builder()
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .description(book.getDescription())
                .author(book.getAuthor())
                .tags(book.getBookTags().stream()
                        .map(bt -> bt.getTag().getTagName())
                        .collect(Collectors.toSet()))
                .publishedDate(book.getPublicationDate())
                .sellingPrice(book.getSellingPrice())
                .averageRating(book.getAverageRating())
                .reviewCount(book.getReviewCount())
                .viewCount(book.getViewCount())
                .searchCount(book.getSearchCount())
                .popularity(book.getPopularity())
                .imageUrl(book.getImageUrl())// 검색 횟수는 처음엔 0
                .build();
    }


}

