package io.urdego.urdego_content_service.domain.entity.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.urdego.urdego_content_service.api.controller.dto.response.ContentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


import static io.urdego.urdego_content_service.domain.entity.QContent.content;

@Component
@RequiredArgsConstructor
public class ContentRepositoryImpl implements ContentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // userId를 통해 해당 유저의 컨텐츠를 조회한다.
    @Override
    public List<ContentResponse> findUserContentsByUserId_CursorPaging(
            Long userId, Long cursorIdx, Long limit) {

        JPAQuery<ContentResponse> query =
                queryFactory.select(Projections.constructor(ContentResponse.class,
                                content.id,
                                content.url,
                                content.contentName,
                                content.address,
                                content.latitude,
                                content.longitude,
                                content.hint))
                        .from(content)
                        .where(content.userId.eq(userId))
                        .orderBy(content.id.asc());

        if (cursorIdx != null) {
            query = query.where(content.id.gt(cursorIdx)); // 먼저 등록한순 (마지막 항목기준 Id 값이 큰값들 조회)
        }

        return query.limit(limit).fetch();
    }

    // userId를 통해 유저의 전체 컨텐츠수를 반환한다.
    @Override
    public Long countUserContentsByUserId(Long userId) {
        return queryFactory.select(content.id.count())
                .from(content)
                .where(content.userId.eq(userId))
                .fetchOne();
    }
}
