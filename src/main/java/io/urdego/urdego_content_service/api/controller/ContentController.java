package io.urdego.urdego_content_service.api.controller;


import io.urdego.urdego_content_service.api.controller.dto.request.ContentSaveRequest;
import io.urdego.urdego_content_service.domain.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/content-service")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    // 컨텐츠 저장
    // Todo: Security 인증 로직 필요
    @PostMapping("/{userId}")
    public ResponseEntity<Void> saveContent(@ModelAttribute ContentSaveRequest request,
                                            @PathVariable Long userId) {


        contentService.saveContent(userId, request);

        return ResponseEntity.ok().build();
    }
}
