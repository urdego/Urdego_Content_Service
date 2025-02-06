package io.urdego.urdego_content_service.api.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ContentUpdateRequest {

    private String contentName;

    private String address;

    private Double latitude;

    private Double longitude;

    private String hint;
}
