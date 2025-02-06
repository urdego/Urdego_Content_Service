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
    public List<ContentResponse> findUserContentsByUserId_CursorPaging(Long userId, Long cursorIdx, Long limit, String sortBy) {


        JPAQuery<ContentResponse> query =
                queryFactory.select(Projections.constructor(ContentResponse.class,
                                content.id,
                                content.url,
                                content.contentName,
                                content.address,
                                content.latitude,
                                content.longitude,
                                content.hint,
                                content.createdDateTime))
                        .from(content)
                        .where(content.userId.eq(userId));

        if(sortBy.equals("recent"))
        {
            query = query.orderBy(content.createdDateTime.desc(), content.id.desc());

            if (cursorIdx != null) {
                // 커서 기반 페이징: contentId가 cursorIdx보다 작은 데이터만 조회 (최신순)
                query = query.where(content.id.lt(cursorIdx));
            }
        } else if(sortBy.equals("oldest"))
        {
            query = query.orderBy(content.createdDateTime.asc(), content.id.asc());

            if (cursorIdx != null) {
                query = query.where(content.id.gt(cursorIdx)); // 먼저 등록한순 (마지막 항목기준 Id 값이 큰값들 조회)
            }
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


    // userId를 통해 해당 유저의 컨텐츠를 조회한다.
    @Override
    public List<ContentResponse> findUserContentsByUserId(Long userId) {

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
                        .where(content.userId.eq(userId));

        return query.fetch();
    }
}
