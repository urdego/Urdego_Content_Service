package io.urdego.urdego_content_service.common.exception;

import feign.FeignException;
import io.urdego.urdego_content_service.common.exception.content.UserContentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    // 유저 컨텐츠 예외 처리
    @ExceptionHandler(UserContentException.class)
    public ResponseEntity<ErrorResponse> handleUserContentException(UserContentException e) {

        log.error("UserContentException: {}", e.getMessage(), e); // 로그 추가
        ErrorResponse response =
                ErrorResponse.from(BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(), e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /*
       데이터 바인딩 중 발생하는 에러 BindException 처리
    */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> bindException(BindException e) {
        String errorMsg =
                e.getBindingResult().getFieldErrors().stream()
                        .map(
                                fieldError ->
                                        fieldError.getField()
                                                + ": "
                                                + fieldError.getDefaultMessage())
                        .collect(Collectors.joining(", "));

        ErrorResponse error =
                ErrorResponse.from(BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(), errorMsg);

        return ResponseEntity.badRequest().body(error);
    }

    /*
       @Valid 어노테이션을 사용한 DTO의 유효성 검사에서 예외가 발생한 경우
    */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e) {
        String errorMsg =
                e.getBindingResult().getFieldErrors().stream()
                        .map(
                                fieldError ->
                                        fieldError.getField()
                                                + ": "
                                                + fieldError.getDefaultMessage())
                        .collect(Collectors.joining(", "));

        ErrorResponse error =
                ErrorResponse.from(BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(), errorMsg);

        return ResponseEntity.badRequest().body(error);
    }

    /*
        OpenFeign Exception 처리
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException e) {

        String errorMsg = e.getMessage();

        ErrorResponse error =
                ErrorResponse.from(e.status(), BAD_REQUEST.getReasonPhrase(), errorMsg);

        return ResponseEntity.badRequest().body(error);
    }
}

