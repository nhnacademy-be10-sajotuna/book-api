package com.sajotuna.books.repository.impl;

import com.querydsl.core.QueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sajotuna.books.model.QTag;
import com.sajotuna.books.model.Tag;
import com.sajotuna.books.repository.CustomTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomTagRepositoryImpl implements CustomTagRepository {

        private final JPAQueryFactory queryFactory;
//
//    @Override
//    public Optional<Tag> findByTagName(String tagName) {
//        QTag tag = QTag.tag;
//        Tag tags = queryFactory
//                .selectFrom(tag)
//                .where(tag.tagName.contains(tagName))
//                .fetchOne();
//        return Optional.ofNullable(tags);
//    }
}
