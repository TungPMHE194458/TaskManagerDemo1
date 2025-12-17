package com.example.TaskManagerDemo1.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

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

    @ManyToOne
    @JoinColumn(name = "user_id")
    Users user;
}
