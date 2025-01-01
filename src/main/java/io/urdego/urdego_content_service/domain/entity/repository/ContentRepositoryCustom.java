package io.urdego.urdego_content_service.domain.entity.repository;

import io.urdego.urdego_content_service.api.controller.dto.response.ContentResponse;

import java.util.List;

public interface ContentRepositoryCustom {

    // userId를 통해 해당 유저의 컨텐츠를 조회한다.
    List<ContentResponse> findUserContentsByUserId_CursorPaging(Long userId, Long cursorIdx, Long limit);

    // userId를 통해 유저의 전체 컨텐츠수를 반환한다.
    Long countUserContentsByUserId(Long userId);
}
