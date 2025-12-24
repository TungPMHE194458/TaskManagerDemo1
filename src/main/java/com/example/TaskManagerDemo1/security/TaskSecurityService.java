package com.example.TaskManagerDemo1.security;
import com.example.TaskManagerDemo1.enums.MemberStatus;
import com.example.TaskManagerDemo1.enums.TaskRole;
import com.example.TaskManagerDemo1.repository.UserTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("taskSecurity")
@RequiredArgsConstructor
public class TaskSecurityService {

    private final UserTaskRepository userTaskRepository;



    public boolean isTaskOwner(int taskId, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated())
            return false;

        return userTaskRepository
                .findByTask_IDAndRoleAndStatus(
                        taskId,
                        TaskRole.OWNER,
                        MemberStatus.ACCEPTED
                )
                .map(ut -> ut.getUser().getUsername()
                        .equals(authentication.getName()))
                .orElse(false);
    }


    public boolean isTaskMember(int taskId, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated())
            return false;

        return userTaskRepository.existsByTask_IDAndUser_UsernameAndStatus(
                taskId,
                authentication.getName(),
                MemberStatus.ACCEPTED
        );
    }
}
