package io.urdego.urdego_content_service.domain.service;

import io.urdego.urdego_content_service.api.controller.dto.request.ContentSaveRequest;
import io.urdego.urdego_content_service.domain.entity.Content;
import io.urdego.urdego_content_service.domain.entity.repository.ContentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final ContentRepository contentRepository;

    // 컨텐츠 저장
    @Override
    public void saveContent(Long userId, ContentSaveRequest request) {

        String savedPath = ContentCommander.saveContent(userId, request.getContentName(), request.getContent());

        Content content = Content.builder()
                .userId(userId)
                .url(savedPath)
                .contentName(request.getContentName())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .hint(request.getHint())
                .build();
        contentRepository.save(content);
    }
}
