package com.example.TaskManagerDemo1.security;

import com.example.TaskManagerDemo1.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("taskSecurity")
public class TaskSecurityService {

    @Autowired
    private TaskRepository taskRepository;

    public boolean isTaskOwner(int taskId, Authentication authentication) {
        return taskRepository.findById(taskId)
                .map(task ->
                        task.getUser()
                                .getUsername()
                                .equals(authentication.getName())
                )
                .orElse(false);
    }
}
