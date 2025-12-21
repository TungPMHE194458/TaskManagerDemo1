package com.example.TaskManagerDemo1.repository;

import com.example.TaskManagerDemo1.entity.Tasks;
import com.example.TaskManagerDemo1.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Tasks, Integer> {
    void deleteByParentTask(Tasks parent);
    List<Tasks> findAllByParentTask(Tasks parent);

}
