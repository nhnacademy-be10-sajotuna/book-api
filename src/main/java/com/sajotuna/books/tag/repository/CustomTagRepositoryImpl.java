package com.sajotuna.books.tag.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
