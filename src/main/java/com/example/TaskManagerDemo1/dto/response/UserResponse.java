package com.example.TaskManagerDemo1.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)

public class UserResponse {
    int ID;
    String username;
    String firstName;
    String lastName;
    Set<String> roles;
}
