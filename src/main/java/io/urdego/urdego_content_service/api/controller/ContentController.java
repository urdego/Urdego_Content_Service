package io.urdego.urdego_content_service.api.controller;


import io.urdego.urdego_content_service.domain.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/content-service")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;
}
