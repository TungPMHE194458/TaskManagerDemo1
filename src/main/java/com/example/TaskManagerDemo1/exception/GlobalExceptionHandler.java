package com.example.TaskManagerDemo1.exception;

import com.example.TaskManagerDemo1.dto.response.ApiResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Bắt lỗi do AppException (lỗi nghiệp vụ)
    @ExceptionHandler(AppException.class)
    public ApiResponse<?> handleAppException(AppException ex) {

        ApiResponse<Object> response = new ApiResponse<>();
        response.setCode(ex.getErrorCode().getCode());
        response.setMessage(ex.getErrorCode().getMessage());
        response.setResult(null);

        return response;
    }

    // Bắt tất cả lỗi còn lại (runtime, null pointer, ...)
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception ex) {

        ApiResponse<Object> response = new ApiResponse<>();
        response.setCode(ErrorCode.INTERNAL_ERROR.getCode());
        response.setMessage(ErrorCode.INTERNAL_ERROR.getMessage());
        response.setResult(null);

        return response;
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidationException(MethodArgumentNotValidException ex) {

        String errorKey = ex.getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        ErrorCode errorCode = ErrorCode.valueOf(errorKey);

        ApiResponse<Object> response = new ApiResponse<>();
        response.setCode(errorCode.getCode());
        response.setMessage(errorCode.getMessage());
        response.setResult(null);

        return response;
    }
}
