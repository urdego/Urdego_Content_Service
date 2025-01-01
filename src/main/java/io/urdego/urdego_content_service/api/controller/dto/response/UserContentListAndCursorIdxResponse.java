package io.urdego.urdego_content_service.api.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class UserContentListAndCursorIdxResponse {

    private Long userId;

    private List<ContentResponse> contents; // 컨텐츠 정보

    private Long cursorIdx;

    private Long totalContent;

    public void setNextCursorIdx() {
        cursorIdx =
                contents == null || contents.isEmpty()
                        ? 0L
                        : contents.get(contents.size() - 1).getContentId();
    }

}
