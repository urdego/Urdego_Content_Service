package io.urdego.urdego_content_service.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {

    // Content
    USER_CONTENT_NOT_FOUND("존재하지 않는 유저 컨텐츠입니다."),
    GAME_CONTENT_NOT_ENOUGH("게임을 시작하는데 필요한 컨텐츠가 부족합니다."),

    // Content save
    INVALID_FILE_FORMAT("잘못된 형식의 컨텐츠입니다."),
    CONTENT_SAVE_FAILED("컨텐츠 저장에 실패했습니다."),
    CONTENT_MULTI_SAVE_FAILED("컨텐츠 다중 저장에 실패했습니다. (컨텐츠 유해성 감지)"),
    CONTENT_DELETE_FAILED("컨텐츠 삭제에 실패했습니다."),
    DIRECTORY_CREATION_FAILED("상위 폴더 생성 중 예외가 발생했습니다."),
    CONTENT_NOT_ALLOWED("컨텐츠에 유해성이 감지되어 저장에 실패했습니다."),
    CONTENT_ANALYSIS_FAILED("컨텐츠 분석에 실패했습니다."),
    CONTENT_UPDATE_FAILED("컨텐츠 수정에 실패했습니다."),

    // Tensorflow
    GRAPH_LOAD_FAILED("GraphDef 파일을 로드하는 데 실패했습니다"),

    // Image
    IMAGE_METADATA_FAILED("이미지 메타데이터 추출에 실패했습니다.");

    private final String text;
}
