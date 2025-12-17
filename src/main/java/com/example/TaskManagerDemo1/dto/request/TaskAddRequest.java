package com.example.TaskManagerDemo1.dto.request;

import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskAddRequest {
    String title;
    String description;
    String status;
    String priority;
    LocalDate deadline;
    Integer parentTaskId;
}
