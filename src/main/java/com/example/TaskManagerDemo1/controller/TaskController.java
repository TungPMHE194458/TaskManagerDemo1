package com.example.TaskManagerDemo1.controller;

import com.example.TaskManagerDemo1.dto.response.ApiResponse;
import com.example.TaskManagerDemo1.dto.request.TaskAddRequest;
import com.example.TaskManagerDemo1.dto.request.TaskUpdateRequest;
import com.example.TaskManagerDemo1.dto.response.TaskResponse;
import com.example.TaskManagerDemo1.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping
    public ApiResponse<TaskResponse> createTask(
            @RequestBody TaskAddRequest request) {
        return taskService.createTask(request);
    }
    @GetMapping
    public ApiResponse<List<TaskResponse>> getMyTasks() {
        return taskService.getMyRootTasks();
    }
    /* GET SUBTASK */
    @GetMapping("/{taskId}/subtasks")
    public ApiResponse<List<TaskResponse>> getSubTasks(
            @PathVariable int taskId) {
        return taskService.getSubTasks(taskId);
    }

    /* UPDATE TASK */
    @PutMapping("/{taskId}")
    public ApiResponse<TaskResponse> updateTask(
            @PathVariable int taskId,
            @RequestBody TaskUpdateRequest request) {
        return taskService.updateTask(taskId, request);
    }

    /* DELETE TASK */
    @DeleteMapping("/{taskId}")
    public ApiResponse<String> deleteTask(
            @PathVariable int taskId) {
        return taskService.deleteTask(taskId);
    }
}
