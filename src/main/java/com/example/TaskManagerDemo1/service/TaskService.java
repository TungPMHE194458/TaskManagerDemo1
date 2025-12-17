package com.example.TaskManagerDemo1.service;

import com.example.TaskManagerDemo1.dto.request.TaskAddRequest;
import com.example.TaskManagerDemo1.dto.request.TaskUpdateRequest;
import com.example.TaskManagerDemo1.dto.response.ApiResponse;
import com.example.TaskManagerDemo1.entity.Tasks;
import com.example.TaskManagerDemo1.entity.Users;
import com.example.TaskManagerDemo1.exception.AppException;
import com.example.TaskManagerDemo1.exception.ErrorCode;
import com.example.TaskManagerDemo1.repository.TaskRepository;
import com.example.TaskManagerDemo1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * CREATE TASK (USER / ADMIN)
     */
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ApiResponse<Tasks> addTask(TaskAddRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Users user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Tasks task = new Tasks();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDeadline(request.getDeadline());
        task.setUser(user);

        return ApiResponse.success(taskRepository.save(task));
    }

    /**
     * GET ALL TASKS (ADMIN)
     */
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Tasks>> getAllTasks() {
        return ApiResponse.success(taskRepository.findAll());
    }

    /**
     * GET TASK BY ID (OWNER or ADMIN)
     */
    @PreAuthorize("@taskSecurity.isTaskOwner(#id, authentication) or hasRole('ADMIN')")
    public ApiResponse<Tasks> getTaskById(int id) {

        Tasks task = taskRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        return ApiResponse.success(task);
    }

    /**
     * UPDATE TASK (OWNER or ADMIN)
     */
    @PreAuthorize("@taskSecurity.isTaskOwner(#taskId, authentication) or hasRole('ADMIN')")
    public ApiResponse<Tasks> updateTask(int taskId, TaskUpdateRequest request) {

        Tasks task = taskRepository.findById(taskId)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDeadline(request.getDeadline());

        return ApiResponse.success(taskRepository.save(task));
    }

    /**
     * DELETE TASK (OWNER or ADMIN)
     */
    @PreAuthorize("@taskSecurity.isTaskOwner(#id, authentication) or hasRole('ADMIN')")
    public ApiResponse<String> deleteTaskById(int id) {

        Tasks task = taskRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        taskRepository.delete(task);
        return ApiResponse.success("Task " + id + " has been deleted");
    }

    /**
     * GET TASKS BY USER (SELF or ADMIN)
     */
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isUserSelf(#userId, authentication)")
    public ApiResponse<List<Tasks>> getTasksByUserId(Integer userId) {
        return ApiResponse.success(taskRepository.findByUser_ID(userId));
    }
}
