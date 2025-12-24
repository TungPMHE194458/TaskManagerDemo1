package com.example.TaskManagerDemo1.repository;

import com.example.TaskManagerDemo1.entity.Tasks;
import com.example.TaskManagerDemo1.entity.UserTask;
import com.example.TaskManagerDemo1.entity.Users;
import com.example.TaskManagerDemo1.enums.MemberStatus;
import com.example.TaskManagerDemo1.enums.TaskRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserTaskRepository extends JpaRepository<UserTask, Integer> {
    Optional<UserTask> findByTaskAndUser(Tasks task, Users user);
    boolean existsByTaskAndUser(Tasks task, Users user);

    Optional<UserTask> findByUserAndStatus(Users user, MemberStatus status);

    Optional<UserTask> findByTaskAndUserAndStatus(Tasks task, Users user, MemberStatus status);

    Optional<UserTask> findByUserAndTask(Users user, Tasks task);

    Optional<UserTask> findByTaskAndRoleAndStatus(Tasks task, TaskRole role, MemberStatus status);


    List<UserTask> findByTaskAndRole(Tasks task, TaskRole taskRole);

    Optional<UserTask> findFirstByTaskAndRole(Tasks task, TaskRole role);



    boolean existsByTask_IDAndUser_UsernameAndStatus(
            int taskId,
            String username,
            MemberStatus status
    );

    Optional<UserTask> findByTask_IDAndRoleAndStatus(
            int taskId,
            TaskRole role,
            MemberStatus status
    );

}
