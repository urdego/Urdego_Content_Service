package io.urdego.urdego_content_service.domain.service;

import io.urdego.urdego_content_service.api.controller.dto.request.ContentSaveRequest;
import io.urdego.urdego_content_service.api.controller.dto.response.ContentResponse;
import io.urdego.urdego_content_service.api.controller.dto.response.UserContentListAndCursorIdxResponse;
import io.urdego.urdego_content_service.common.exception.ExceptionMessage;
import io.urdego.urdego_content_service.common.exception.content.UserContentException;
import io.urdego.urdego_content_service.domain.entity.Content;
import io.urdego.urdego_content_service.domain.entity.repository.ContentRepository;
import io.urdego.urdego_content_service.domain.service.dto.FileInfo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private static final Long MAX_LIMIT = 1L;
    private final ContentRepository contentRepository;

    // 컨텐츠 저장
    @Override
    public void saveContent(Long userId, ContentSaveRequest request) {

        FileInfo fileInfo = ContentCommander.saveContent(userId, request.getContent());

        Content content = Content.builder()
                .userId(userId)
                .url(fileInfo.getSavedPath())
                .contentName(request.getContentName())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .hint(request.getHint())
                .fileName(fileInfo.getFileName())
                .build();
        contentRepository.save(content);
    }

    // 컨텐츠 삭제
    @Override
    public void deleteContent(Long contentId) {

        Content content = findUserContentByIdOrException(contentId);

        try {
            // 파일 삭제
            ContentCommander.deleteContent(content.getUserId(), content.getFileName());

            // 엔티티 삭제
            contentRepository.delete(content);

        } catch (Exception e) {
            throw new UserContentException(ExceptionMessage.CONTENT_DELETE_FAILED);
        }
    }

    @Override
    // 컨텐츠 조회
    public UserContentListAndCursorIdxResponse getUserContents(Long userId, Long cursorIdx, Long limit) {

        limit = Math.max(limit, MAX_LIMIT);

        List<ContentResponse> userContents = contentRepository.findUserContentsByUserId_CursorPaging(userId, cursorIdx, limit);

        // 총 컨텐츠 수 조회
        Long totalContent = contentRepository.countUserContentsByUserId(userId);

        // 컨텐츠가 비어있을경우 빈 배열 반환
        if (userContents.isEmpty()) {

            return UserContentListAndCursorIdxResponse.builder()
                    .contents(Collections.emptyList())
                    .userId(userId)
                    .build();
        }

        UserContentListAndCursorIdxResponse response =
                UserContentListAndCursorIdxResponse.builder()
                        .contents(userContents)
                        .totalContent(totalContent)
                        .userId(userId)
                        .build();
        response.setNextCursorIdx();

        return response;
    }


    // 컨텐츠 엔티티 조회
    private Content findUserContentByIdOrException(Long contentId) {
        return contentRepository
                .findById(contentId).orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", contentId, ExceptionMessage.USER_CONTENT_NOT_FOUND);
                    throw new UserContentException(ExceptionMessage.USER_CONTENT_NOT_FOUND);
                });
    }
}
