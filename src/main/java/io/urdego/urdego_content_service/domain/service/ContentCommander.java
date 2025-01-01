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
import java.util.List;
import java.util.UUID;

public class ContentCommander {

    public static final Path BASE_PATH = Path.of("/urdego/user/content");
    public static final String BASE_URL = "http://urdego.site/urdego/user/content";

    public static String saveContent(Long userId, String contentName, MultipartFile content) {
        Path savedContentPath = BASE_PATH.resolve(Path.of(String.valueOf(userId), contentName));
        createParentDirectories(savedContentPath);

        String filename = createFilename(userId, content.getOriginalFilename());
        try (InputStream is = content.getInputStream();
             OutputStream os = Files.newOutputStream(savedContentPath)) {

            // 컨텐츠 저장
            StreamUtils.copy(is, os);
        } catch (IOException e) {
            throw new UserContentException(ExceptionMessage.CONTENT_SAVE_FAILED);
        }

        return BASE_URL + "/" + userId + "/" + filename;
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

    // 파일이름 생성
    private static String createFilename(Long userId, String filename) {
        try {
            String extension = getFileExtension(filename);
            return userId + "_" + UUID.randomUUID() + extension; // 유저 ID + UUID + 확장자

        } catch (IllegalArgumentException e) {
            throw new UserContentException(ExceptionMessage.INVALID_FILE_FORMAT);
        }
    }

    // 파일 확장자 추출
    private static String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new UserContentException(ExceptionMessage.INVALID_FILE_FORMAT);
        }
        String extension = filename.substring(filename.lastIndexOf(".")).toLowerCase();

        // 유효한 확장자
        List<String> validExtensions = List.of(".jpg", ".jpeg", ".png", ".mp4", ".mov");

        // 확장자가 유효하지 않으면 예외 발생
        if (!validExtensions.contains(extension.toLowerCase())) {
            throw new UserContentException(ExceptionMessage.INVALID_FILE_FORMAT);
        }

        return extension;
    }
}
