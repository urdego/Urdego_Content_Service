package io.urdego.urdego_content_service.api.controller.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@Builder
public class ContentSaveRequest {

    private MultipartFile content;

    private String contentName;

    private String address;

    private Double latitude;

    private Double longitude;

    private String hint;
}
