package com.example.TaskManagerDemo1.repository;

import com.example.TaskManagerDemo1.entity.Tasks;
import com.example.TaskManagerDemo1.entity.UserTask;
import com.example.TaskManagerDemo1.entity.Users;
import com.example.TaskManagerDemo1.enums.TaskRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserTaskRepository extends JpaRepository<UserTask, Integer> {

    List<UserTask> findByUser(Users user);

    Optional<UserTask> findByUserAndTask(Users user, Tasks task);

    List<UserTask> findByTask(Tasks task);

    Optional<UserTask> findByTaskAndUser(Tasks task, Users user);

    List<UserTask> findByTaskAndRole(Tasks task, TaskRole taskRole);

    Optional<UserTask> findFirstByTaskAndRole(Tasks task, TaskRole role);

    void deleteByTask(Tasks task);

    @Query("""
        select ut from UserTask ut
        where ut.task.ID = :taskId
          and ut.role = 'OWNER'
    """)
    Optional<UserTask> findOwnerByTaskId(@Param("taskId") int taskId);

    @Query("""
        select count(ut) > 0 from UserTask ut
        where ut.task.ID = :taskId
          and ut.user.username = :username
    """)
    boolean existsByTaskIdAndUsername(
            @Param("taskId") int taskId,
            @Param("username") String username
    );


}
