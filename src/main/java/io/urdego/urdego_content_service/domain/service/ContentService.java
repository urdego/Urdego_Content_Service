package io.urdego.urdego_content_service.domain.service;

import io.urdego.urdego_content_service.api.controller.dto.request.ContentSaveRequest;

public interface ContentService {

    // 컨텐츠 저장
    void saveContent(Long userId, ContentSaveRequest request);

    // 컨텐츠 삭제
    void deleteContent(Long contentId);
}
