package io.urdego.urdego_content_service.domain.service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FileInfo {

    private String fileName;

    private String savedPath;
}
