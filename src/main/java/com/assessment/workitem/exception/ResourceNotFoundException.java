package com.assessment.workitem.exception;

/**
 * 资源不存在异常
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(404, message);
    }

    public ResourceNotFoundException(String resource, Long id) {
        super(404, String.format("%s 不存在，ID: %d", resource, id));
    }
}
