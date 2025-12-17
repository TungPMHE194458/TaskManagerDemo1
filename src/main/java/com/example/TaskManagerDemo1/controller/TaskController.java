package com.example.TaskManagerDemo1.controller;

import com.example.TaskManagerDemo1.dto.response.ApiResponse;
import com.example.TaskManagerDemo1.dto.request.TaskAddRequest;
import com.example.TaskManagerDemo1.dto.request.TaskUpdateRequest;
import com.example.TaskManagerDemo1.entity.Tasks;
import com.example.TaskManagerDemo1.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping
    public ApiResponse<Tasks> addTask(@RequestBody TaskAddRequest request ){
       return taskService.addTask(request);
    }

    @GetMapping
    public ApiResponse<List<Tasks>> getAllTasks(){
        return taskService.getAllTasks();
    }
    @GetMapping("/{taskId}")
    public ApiResponse<Tasks> getTaskById(@PathVariable int taskId){
       return taskService.getTaskById(taskId);
    }
    @GetMapping("/user/{userId}")
    public ApiResponse<List<Tasks>> getUserTasks(@PathVariable int userId){
        return taskService.getTasksByUserId(userId);
    }

    @PutMapping("/{taskId}")
    public ApiResponse<Tasks> updateTask(@PathVariable("taskId") int taskId, @RequestBody TaskUpdateRequest request){
        return taskService.updateTask(taskId, request);
    }

    @DeleteMapping("/{taskId}")
    public ApiResponse<String> deleteTask(@PathVariable("taskId") int taskId){
       return taskService.deleteTaskById(taskId);
    }
}
