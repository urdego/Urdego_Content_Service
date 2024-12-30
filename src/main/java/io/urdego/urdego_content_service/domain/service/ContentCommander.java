package io.urdego.urdego_content_service.domain.service;

import io.urdego.urdego_content_service.common.exception.ExceptionMessage;
import io.urdego.urdego_content_service.common.exception.content.UserContentException;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ContentCommander {

    public static final Path BASE_PATH = Path.of("/urdego/user/content");

    public static String saveContent(Long userId, String contentName, MultipartFile content) {
        Path savedContentPath = BASE_PATH.resolve(Path.of(String.valueOf(userId), contentName));
        createParentDirectories(savedContentPath);
        try (InputStream is = content.getInputStream();
             OutputStream os = Files.newOutputStream(savedContentPath)) {

            // 컨텐츠 저장
            StreamUtils.copy(is, os);
        } catch (IOException e) {
            throw new UserContentException(ExceptionMessage.CONTENT_SAVE_FAILED);
        }

        // 상대 경로 반환 (userId/contentName)
        return savedContentPath.toString();
    }

    private static void createParentDirectories(Path contentPath) {
        Path parentPath = contentPath.getParent();
        if (!Files.exists(parentPath)) {
            try {
                Files.createDirectories(parentPath);
            } catch (IOException e) {
                throw new UserContentException(ExceptionMessage.DIRECTORY_CREATION_FAILED);
            }
        }
    }
}
