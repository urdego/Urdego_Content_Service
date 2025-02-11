package io.urdego.urdego_content_service.api.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.urdego.urdego_content_service.api.controller.dto.request.ContentMultiSaveRequest;
import io.urdego.urdego_content_service.api.controller.dto.request.ContentSaveRequest;
import io.urdego.urdego_content_service.api.controller.dto.request.ContentUpdateRequest;
import io.urdego.urdego_content_service.api.controller.dto.response.UserContentList;
import io.urdego.urdego_content_service.api.controller.dto.response.UserContentListAndCursorIdxResponse;
import io.urdego.urdego_content_service.domain.service.ContentService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/content-service")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    // 컨텐츠 저장
    @Tag(name = "컨텐츠 API")
    @Operation(summary = "컨텐츠 저장", description = "userId와 컨텐츠 그리고 사용자 입력을 받아 컨텐츠를 저장",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "multipart/form-data",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ContentSaveRequest.class))))
    // 스웨거에서 multipart/form-data 형식을 처리
    @PostMapping("/{userId}")
    public ResponseEntity<Void> saveContent(@ModelAttribute ContentSaveRequest request,
                                            @PathVariable Long userId) {


        contentService.saveContent(userId, request);

        return ResponseEntity.ok().build();
    }

    // 여러 컨텐츠 저장
    @Tag(name = "컨텐츠 API")
    @Operation(summary = "컨텐츠 다중 저장", description = "userId와 다중 컨텐츠 그리고 사용자 입력을 받아 컨텐츠를 저장",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "multipart/form-data",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ContentMultiSaveRequest.class))))
    @PostMapping("/{userId}/multiple")
    public ResponseEntity<Void> saveContentMulti(@ModelAttribute ContentMultiSaveRequest request,
                                                 @PathVariable Long userId) {
        contentService.saveMultiContent(userId, request);

        return ResponseEntity.ok().build();
    }

    // 컨텐츠 수정
    @Tag(name = "컨텐츠 API")
    @Operation(summary = "컨텐츠 수정", description = "contentId로 컨텐츠 개별 수정")
    @PatchMapping(value = "{userId}/content/{contentId}")
    public ResponseEntity<Void> updateContent(@PathVariable Long userId,
                                              @PathVariable Long contentId,
                                              @RequestBody ContentUpdateRequest request) {



        contentService.updateContent(userId, contentId, request);
        return ResponseEntity.ok().build();
    }


    // 컨텐츠 삭제
    @Tag(name = "컨텐츠 API")
    @Operation(summary = "컨텐츠 삭제", description = "userId와 contentId를 가지고 컨텐츠 개별 삭제")
    @DeleteMapping(value = "{userId}/content/{contentId}")
    public ResponseEntity<Void> deleteContent(@PathVariable Long userId,
                                              @PathVariable Long contentId) {

        contentService.deleteContent(userId, contentId);
        return ResponseEntity.ok().build();
    }

    // 컨텐츠 조회
    @Tag(name = "컨텐츠 API")
    @Operation(summary = "컨텐츠 조회", description = "userId로 사용자의 모든 컨텐츠를 조회")
    @GetMapping(value = "{userId}/contents")
    public ResponseEntity<UserContentListAndCursorIdxResponse> getUserContents(@PathVariable(name = "userId") Long userId,
                                                                               @Min(value = 0) @RequestParam(name = "cursorIdx", required = false) Long cursorIdx,
                                                                               @Min(value = 1) @RequestParam(name = "limit", defaultValue = "5") Long limit,
                                                                               @RequestParam(name = "sortBy", defaultValue = "oldest") String sortBy) {

        UserContentListAndCursorIdxResponse responses = contentService.getUserContents(userId, cursorIdx, limit, sortBy);

        return ResponseEntity.ok().body(responses);
    }

    // 컨텐츠 검색 조회
    @Tag(name = "컨텐츠 API")
    @Operation(summary = "컨텐츠 검색 조회", description = "contentName 검색 조회")
    @GetMapping(value = "{userId}/contents/search")
    public ResponseEntity<UserContentList> getUserContentsName(@PathVariable(name = "userId") Long userId,
                                                               @RequestParam String search) {

        UserContentList responses = contentService.getUserContentsSearch(userId, search);

        return ResponseEntity.ok().body(responses);
    }
}
