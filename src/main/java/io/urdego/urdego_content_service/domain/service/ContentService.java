package io.urdego.urdego_content_service.domain.service;

import io.urdego.urdego_content_service.api.controller.dto.request.ContentMultiSaveRequest;
import io.urdego.urdego_content_service.api.controller.dto.request.ContentSaveRequest;
import io.urdego.urdego_content_service.api.controller.dto.request.ContentUpdateRequest;
import io.urdego.urdego_content_service.api.controller.dto.response.ContentResponse;
import io.urdego.urdego_content_service.api.controller.dto.response.UserContentListAndCursorIdxResponse;

import java.util.List;

public interface ContentService {

    // 컨텐츠 저장
    void saveContent(Long userId, ContentSaveRequest request);

    // 컨텐츠 다중 저장
    void saveMultiContent(Long userId, ContentMultiSaveRequest request);

    // 컨텐츠 수정
    void updateContent(Long userId, Long contentId, ContentUpdateRequest request);

    // 컨텐츠 삭제
    void deleteContent(Long userId, Long contentId);

    // 컨텐츠 조회
    UserContentListAndCursorIdxResponse getUserContents(Long userId, Long cursorIdx, Long limit);

    // 컨텐츠 개별조회 (백엔드 API)
    ContentResponse getContent(Long contentId);

    // 컨텐츠 랜덤조회 (백엔드 API)
    List<ContentResponse> getUrdegoContents(int counts);
}
