package com.example.TaskManagerDemo1.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse<T> {
    int code;
    String message;
    T result;

    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<>(200, "success", result);
    }
}
