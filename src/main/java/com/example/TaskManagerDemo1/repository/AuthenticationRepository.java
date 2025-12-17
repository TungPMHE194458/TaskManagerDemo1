package com.example.TaskManagerDemo1.repository;

import com.example.TaskManagerDemo1.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

public interface AuthenticationRepository extends JpaRepository<Users, Integer> {
    boolean existsByUsername(String username);
    Users findByUsername(String username);
}
