package com.example.TaskManagerDemo1.repository;

import com.example.TaskManagerDemo1.entity.Tasks;
import com.example.TaskManagerDemo1.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Tasks, Integer> {

    List<Tasks> findByUserAndParentTaskIsNull(Users user);

    List<Tasks> findByParentTaskID(int parentTaskID);

    Optional<Tasks> findByIDAndUser(int id, Users user);
}
