package io.urdego.urdego_content_service.common.exception;

public abstract class ContentException extends RuntimeException {
    public ContentException(String message) {
        super(message);
    }
}

