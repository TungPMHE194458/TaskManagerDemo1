package com.example.TaskManagerDemo1.dto.response;

import com.example.TaskManagerDemo1.enums.TaskRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class MemberResponse {
    int id;
    String username;
    TaskRole role;
    String fullName;
}
