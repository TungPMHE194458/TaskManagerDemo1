package com.example.TaskManagerDemo1.controller;

import com.example.TaskManagerDemo1.dto.response.ApiResponse;
import com.example.TaskManagerDemo1.dto.request.TaskAddRequest;
import com.example.TaskManagerDemo1.dto.request.TaskUpdateRequest;
import com.example.TaskManagerDemo1.dto.response.TaskMembersResponse;
import com.example.TaskManagerDemo1.dto.response.TaskResponse;
import com.example.TaskManagerDemo1.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    //ADD NEW TASK
    @PostMapping
    public ApiResponse<TaskResponse> createTask(
            @RequestBody @Valid TaskAddRequest request) {
        return taskService.createTask(request);
    }

    //GET ALL TASK (ADMIN)
    @GetMapping
    public ApiResponse<List<TaskResponse>> getAllTasks(){
        return taskService.getAllTasks();
    }

    //GET ALL ROOT TASKS (PROJECT)
    @GetMapping("/my")
    public ApiResponse<List<TaskResponse>> getMyTasks() {
        return taskService.getMyRootTasks();
    }
    //GET TASK BY TASK ID
    @GetMapping("/{id}")
    public ApiResponse<TaskResponse> getTaskById(@PathVariable int id) {
        return taskService.getTaskById(id);
    }
    /* GET SUBTASK */
    @GetMapping("/{taskId}/subtasks")
    public ApiResponse<List<TaskResponse>> getSubTasks(
            @PathVariable int taskId) {
        return taskService.getSubTasks(taskId);
    }
    //GET MEMBER OF TASK
    @GetMapping("/{id}/members")
    public ApiResponse<TaskMembersResponse> getMembers(
            @PathVariable int id) {
        return taskService.getMembers(id);
    }

    /* UPDATE TASK */
    @PutMapping("/{id}")
    public ApiResponse<TaskResponse> updateTask(
            @PathVariable int id,
            @RequestBody @Valid TaskUpdateRequest request) {
        return taskService.updateTask(id, request);
    }
    /* ===================== DELETE ===================== */

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteTask(@PathVariable int id) {
        return taskService.deleteTask(id);
    }

    /* ===================== MEMBERS ===================== */

    @PostMapping("/{id}/members/{userId}")
    public ApiResponse<String> addMember(
            @PathVariable int id,
            @PathVariable int userId) {
        return taskService.addMember(id, userId);
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ApiResponse<String> removeMember(
            @PathVariable int id,
            @PathVariable int userId) {
        return taskService.removeMember(id, userId);
    }
}
