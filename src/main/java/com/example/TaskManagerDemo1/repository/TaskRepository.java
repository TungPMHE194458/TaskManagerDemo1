package com.example.TaskManagerDemo1.repository;

import com.example.TaskManagerDemo1.entity.Tasks;
import com.example.TaskManagerDemo1.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Tasks, Integer> {
    List<Tasks> findByUser_ID(Integer userId);
}
