package io.urdego.urdego_content_service.api.controller.dto.response;

import io.urdego.urdego_content_service.domain.entity.Content;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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

    private LocalDateTime createdDateTime;


    public static ContentResponse of(Content content) {
        return ContentResponse.builder()
                .contentId(content.getId())
                .url(content.getUrl())
                .contentName(content.getContentName())
                .address(content.getAddress())
                .latitude(content.getLatitude())
                .longitude(content.getLongitude())
                .hint(content.getHint())
                .createdDateTime(content.getCreatedDateTime())
                .build();
    }
}
