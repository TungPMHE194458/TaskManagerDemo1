package com.example.TaskManagerDemo1.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // USER
    USER_NOT_FOUND(101, "User not found"),
    USERNAME_EXISTED(102, "Username already exists"),

    // TASK
    PARENT_TASK_NOT_FOUND(201, "Parent task not found"),
    TASK_NOT_FOUND(202, "Task not found"),
    TASK_ACCESS_DENIED(203, "You do not have permission to access this task"),

    // AUTH
    INVALID_USERNAME_OR_PASSWORD(401, "Invalid username or password"),
    TOKEN_GENERATION_FAILED(500, "Cannot generate token"),
    // VALIDATION
    USERNAME_REQUIRED(400, "Username is required"),
    USERNAME_MIN_4(400, "Username must be at least 4 characters"),

    PASSWORD_REQUIRED(400, "Password is required"),
    PASSWORD_MIN_6(400, "Password must be at least 6 characters"),
    PASSWORD_INVALID_FORMAT(
            400,
            "Password must contain at least 1 uppercase, 1 lowercase, 1 number and 1 special character"
    ),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Access denied"),
    INTERNAL_ERROR(500, "Internal server error");


    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
