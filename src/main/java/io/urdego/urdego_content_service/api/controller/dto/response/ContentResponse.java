package io.urdego.urdego_content_service.api.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ContentResponse {

    private Long contentId;

    private String url;

    private String contentName;

    private String address;

    private Double latitude;

    private Double longitude;

    private String hint;
}
