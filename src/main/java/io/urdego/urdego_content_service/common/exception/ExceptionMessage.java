package io.urdego.urdego_content_service.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {

    // UserContent
    USER_CONTENT_NOT_FOUND("존재하지 않는 유저 콘텐츠입니다."),
    GAME_CONTENT_NOT_ENOUGH("게임을 시작하는데 필요한 컨텐츠가 부족합니다."),

    // AWS
    INVALID_FILE_FORMAT("잘못된 형식의 파일입니다."),
    FILE_UPLOAD_FAILED("파일 업로드에 실패했습니다."),
    CONTENT_DELETE_FAILED("파일 삭제에 실패했습니다."),

    // Image
    IMAGE_METADATA_FAILED("이미지 메타데이터 추출에 실패했습니다.");

    private final String text;
}
