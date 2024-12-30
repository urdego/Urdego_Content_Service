package io.urdego.urdego_content_service.common.exception.content;

import io.urdego.urdego_content_service.common.exception.ContentException;
import io.urdego.urdego_content_service.common.exception.ExceptionMessage;

public class UserContentException extends ContentException {
    public UserContentException(ExceptionMessage message) {
        super(message.getText());
    }
}
