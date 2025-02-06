package io.urdego.urdego_content_service.domain.service;

import io.urdego.urdego_content_service.api.controller.dto.request.ContentMultiSaveRequest;
import io.urdego.urdego_content_service.api.controller.dto.request.ContentSaveRequest;
import io.urdego.urdego_content_service.api.controller.dto.request.ContentUpdateRequest;
import io.urdego.urdego_content_service.api.controller.dto.response.ContentResponse;
import io.urdego.urdego_content_service.api.controller.dto.response.UserContentList;
import io.urdego.urdego_content_service.api.controller.dto.response.UserContentListAndCursorIdxResponse;
import io.urdego.urdego_content_service.common.exception.ExceptionMessage;
import io.urdego.urdego_content_service.common.exception.content.UserContentException;
import io.urdego.urdego_content_service.domain.entity.Content;
import io.urdego.urdego_content_service.domain.entity.repository.ContentRepository;
import io.urdego.urdego_content_service.domain.service.dto.FileInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private static final Long MAX_LIMIT = 1L;
    private static final Long ADMIN = 0L;
    private final ContentRepository contentRepository;

    // 컨텐츠 저장
    @Override
    @Transactional
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

    // 컨텐츠 다중 저장
    @Override
    @Transactional
    public void saveMultiContent(Long userId, ContentMultiSaveRequest request) {

        try {
            List<Content> contentList = new ArrayList<>();

            for (MultipartFile contents : request.getContents()) {
                FileInfo fileInfo = ContentCommander.saveContent(userId, contents); // 파일 저장

                // Content 엔티티 생성
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

                contentList.add(content); // 리스트에 추가
            }
            // 배치 저장
            contentRepository.saveAll(contentList);

        } catch (Exception e) {
            log.error("{}", ExceptionMessage.CONTENT_MULTI_SAVE_FAILED);
            throw new UserContentException(ExceptionMessage.CONTENT_MULTI_SAVE_FAILED);
        }
    }

    // 컨텐츠 수정
    @Override
    @Transactional
    public void updateContent(Long userId, Long contentId, ContentUpdateRequest request) {

        Content content = findUserContentByIdOrException(contentId);

        if (!content.getUserId().equals(userId)) {
            throw new UserContentException(ExceptionMessage.CONTENT_UPDATE_FAILED);
        }
        content.updateContent(request);
    }


    // 컨텐츠 삭제
    @Override
    @Transactional
    public void deleteContent(Long userId, Long contentId) {

        Content content = findUserContentByIdOrException(contentId);

        if (!content.getUserId().equals(userId)) {
            throw new UserContentException(ExceptionMessage.CONTENT_DELETE_FAILED);
        }

        try {
            // 파일 삭제
            ContentCommander.deleteContent(content.getUserId(), content.getFileName());

            // 엔티티 삭제
            contentRepository.delete(content);

        } catch (Exception e) {
            throw new UserContentException(ExceptionMessage.CONTENT_DELETE_FAILED);
        }
    }

    // 컨텐츠 조회
    @Override
    @Transactional(readOnly = true)
    public UserContentListAndCursorIdxResponse getUserContents(Long userId, Long cursorIdx, Long limit, String sortBy) {

        limit = Math.max(limit, MAX_LIMIT);

        List<ContentResponse> userContents = contentRepository.findUserContentsByUserId_CursorPaging(userId, cursorIdx, limit, sortBy);

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

    // 컨텐츠 이름 조회
    @Override
    @Transactional(readOnly = true)
    public UserContentList getUserContentsSearch(Long userId, String search) {

        List<ContentResponse> userContents = contentRepository.findUserContentsBySearch(userId, search);

        // 컨텐츠가 비어있을경우 빈 배열 반환
        if (userContents.isEmpty()) {

            return UserContentList.builder()
                    .contents(Collections.emptyList())
                    .userId(userId)
                    .build();
        }

        return UserContentList.builder()
                .contents(userContents)
                .userId(userId)
                .build();
    }

    // 개별 컨텐츠 조회 (백엔드 API)
    @Override
    @Transactional(readOnly = true)
    public ContentResponse getContent(Long contentId) {

        Content content = findUserContentByIdOrException(contentId);

        return ContentResponse.of(content);
    }

    // 어데고 컨텐츠 랜덤 조회 (백엔드 API)
    @Override
    @Transactional(readOnly = true)
    public List<ContentResponse> getUrdegoContents(int counts) {

        // 어데고 컨텐츠 조회
        List<ContentResponse> urdegoContents = contentRepository.findUserContentsByUserId(ADMIN);

        // 데이터가 3개미만 예외처리
        if (urdegoContents.size() < 3) {
            throw new UserContentException(ExceptionMessage.GAME_CONTENT_NOT_ENOUGH);
        }

        // contentName을 기준으로 그룹화
        Map<String, List<ContentResponse>> groupedByName = urdegoContents.stream()
                .collect(Collectors.groupingBy(ContentResponse::getContentName));

        // 그룹화된 데이터를 처리
        List<List<ContentResponse>> groupedList = groupedByName.values().stream()
                .map(group -> {
                    // 그룹 내부를 셔플 후 최대 3개 선택
                    Collections.shuffle(group);
                    return group.stream().limit(3).toList();
                })
                .collect(Collectors.toList());

        // 그룹 순서를 셔플
        Collections.shuffle(groupedList);

        // 결과 리스트 생성
        List<ContentResponse> result = new ArrayList<>();
        groupedList.forEach(result::addAll);

        // counts에 맞게 제한
        return result.stream()
                .limit(counts)
                .collect(Collectors.toList());
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
