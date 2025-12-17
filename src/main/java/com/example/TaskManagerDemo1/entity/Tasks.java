package com.example.TaskManagerDemo1.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "tasks")
public class Tasks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int ID;

    String title;
    String description;
    String status;
    String priority;
    LocalDate deadline;

    /* ---------------- USER ---------------- */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    Users user;

    /* ------------ SELF REFERENCE ------------ */

    // Task cha (null nếu là task gốc)
    @ManyToOne
    @JoinColumn(name = "parent_id")
    Tasks parentTask;

    // Các subtask
    @OneToMany(mappedBy = "parentTask",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    List<Tasks> subTasks;
}

