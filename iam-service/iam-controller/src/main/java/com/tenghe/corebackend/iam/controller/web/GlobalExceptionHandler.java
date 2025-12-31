package com.tenghe.corebackend.iam.controller.web;

import com.tenghe.corebackend.iam.api.dto.common.ApiResponse;
import com.tenghe.corebackend.iam.application.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理入口，统一转换为 API 响应。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 处理业务异常。
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * 处理未捕获的系统异常。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("系统异常"));
    }
}
