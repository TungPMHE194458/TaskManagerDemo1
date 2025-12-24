package com.example.TaskManagerDemo1.dto.request;

import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskAddRequest {
    @NotBlank(message = "Title must not be blank")
    @Size(max = 100, message = "Title must be at most 100 characters")
    String title;

    @Size(max = 500, message = "Description must be at most 500 characters")
    String description;

    @Pattern(
            regexp = "TODO|IN_PROGRESS|DONE",
            message = "Status must be TODO, IN_PROGRESS or DONE"
    )
    String status;

    @Pattern(
            regexp = "LOW|MEDIUM|HIGH",
            message = "Priority must be LOW, MEDIUM or HIGH"
    )
    String priority;

    @FutureOrPresent(message = "Deadline must be today or in the future")
    LocalDate deadline;

    @Positive(message = "Parent task id must be positive")
    Integer parentTaskId;
}
