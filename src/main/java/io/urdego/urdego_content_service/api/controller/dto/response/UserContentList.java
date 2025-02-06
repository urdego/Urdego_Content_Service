package io.urdego.urdego_content_service.api.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class UserContentList {

    private Long userId;

    private List<ContentResponse> contents;
}
