package io.urdego.urdego_content_service.api.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.urdego.urdego_content_service.api.controller.dto.response.ContentResponse;
import io.urdego.urdego_content_service.domain.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/content-service")
@RequiredArgsConstructor
public class ContentGameController {

    private final ContentService contentService;


    @Tag(name = "백엔드 API")
    @Operation(summary = "컨텐츠 개별 조회", description = "contentId로 컨텐츠 세부 조회")
    @GetMapping(value = "/content")
    public ResponseEntity<ContentResponse> getContent(@RequestParam Long contentId) {


        ContentResponse responses = contentService.getContent(contentId);
        return ResponseEntity.ok().body(responses);
    }

    @Tag(name = "백엔드 API")
    @Operation(summary = "컨텐츠 랜덤 조회", description = "어데고 컨텐츠20개 랜덤 조회")
    @GetMapping(value = "/urdego-content")
    public ResponseEntity<List<ContentResponse>> getUrdegoContents(@RequestParam int counts) {

        List<ContentResponse> responses = contentService.getUrdegoContents(counts);
        return ResponseEntity.ok().body(responses);
    }
}
