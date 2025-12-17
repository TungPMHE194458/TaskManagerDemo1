package com.example.TaskManagerDemo1.service;

import com.example.TaskManagerDemo1.dto.request.TaskAddRequest;
import com.example.TaskManagerDemo1.dto.request.TaskUpdateRequest;
import com.example.TaskManagerDemo1.dto.response.ApiResponse;
import com.example.TaskManagerDemo1.dto.response.TaskResponse;
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

    private TaskResponse mapToResponse(Tasks task) {

        return TaskResponse.builder()
                .ID(task.getID())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .deadline(task.getDeadline())
                .userId(task.getUser().getID())
                .parentTaskID(
                        task.getParentTask() != null
                                ? task.getParentTask().getID()
                                : null
                )
                .subTasks(
                        task.getSubTasks() == null
                                ? List.of()
                                : task.getSubTasks()
                                .stream()
                                .map(this::mapToResponse)
                                .toList()
                )
                .build();
    }

    private Users getCurrentUser() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    /**
     * CREATE TASK (USER / ADMIN)
     */
    public ApiResponse<TaskResponse> createTask(TaskAddRequest request) {

        Users user = getCurrentUser();

        Tasks task = new Tasks();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDeadline(request.getDeadline());
        task.setUser(user);
        //case new task has parent
        if (request.getParentTaskId() != null) {
            Tasks parent = taskRepository
                    .findByIDAndUser(request.getParentTaskId(), user)
                    //case user's task is not contain parent task
                    .orElseThrow(() -> new AppException(ErrorCode.PARENT_TASK_NOT_FOUND));
            //case exist parent task in user's task
            task.setParentTask(parent);
        }
        taskRepository.save(task);
        return ApiResponse.success(mapToResponse(task));
    }


    /**
     * GET TASK BY ID (OWNER or ADMIN)
     */
    @PreAuthorize("@taskSecurity.isTaskOwner(#id, authentication) or hasRole('ADMIN')")
    public ApiResponse<TaskResponse> getTaskById(int id) {

        Tasks task = taskRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        return ApiResponse.success(mapToResponse(task));
    }
    /**
     * GET ALL TASKS (ADMIN)
     */

    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<TaskResponse>> getAllTasks() {

        return ApiResponse.success(
                taskRepository.findAll()
                        .stream()
                        .map(this::mapToResponse)
                        .toList()
        );
    }
    /**
     * GET ROOT TASK (task goc)
     */
    public ApiResponse<List<TaskResponse>> getMyRootTasks() {

        Users user = getCurrentUser();

        return ApiResponse.success(
                taskRepository.findByUserAndParentTaskIsNull(user)
                        .stream()
                        .map(this::mapToResponse)
                        .toList()
        );
    }

    /**
     * GET SUB TASK (task goc)
     */
    public ApiResponse<List<TaskResponse>> getSubTasks(int parentId) {
        Users user = getCurrentUser();

        taskRepository.findByIDAndUser(parentId, user)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        return ApiResponse.success(
                taskRepository.findByParentTaskID(parentId)
                        .stream()
                        .map(this::mapToResponse)
                        .toList()
        );
    }
    /**
     * UPDATE TASK (OWNER or ADMIN)
     */
    public ApiResponse<TaskResponse> updateTask(int taskId, TaskUpdateRequest request) {

        Users user = getCurrentUser();

        Tasks task = taskRepository.findByIDAndUser(taskId, user)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDeadline(request.getDeadline());
        taskRepository.save(task);
        return ApiResponse.success(mapToResponse(task));
    }

    /**
     * DELETE TASK (OWNER or ADMIN)
     */
    public ApiResponse<String> deleteTask(int taskId) {

        Users user = getCurrentUser();

        Tasks task = taskRepository.findByIDAndUser(taskId, user)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        taskRepository.delete(task);
        return ApiResponse.success("Deleted successfully");
    }


}
