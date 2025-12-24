package com.example.TaskManagerDemo1.entity;

import com.example.TaskManagerDemo1.enums.MemberStatus;
import com.example.TaskManagerDemo1.enums.TaskRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(
        name = "user_tasks",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "task_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    /* -------- USER -------- */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    Users user;

    /* -------- TASK -------- */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    Tasks task;

    /* -------- ROLE -------- */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    TaskRole role;

    /* -------- STATUS -------- */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    MemberStatus status;
}
