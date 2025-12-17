package com.example.TaskManagerDemo1.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class TaskResponse {

    int ID;
    String title;
    String description;
    String status;
    String priority;
    LocalDate deadline;
    Integer userId;
    Integer parentTaskID;
    List<TaskResponse> subTasks;
}