package com.example.TaskManagerDemo1.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLoginRequest {
    String username;
    String password;
}
