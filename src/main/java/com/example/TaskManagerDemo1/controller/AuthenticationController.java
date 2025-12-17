package com.example.TaskManagerDemo1.controller;

import com.example.TaskManagerDemo1.dto.request.UserLoginRequest;
import com.example.TaskManagerDemo1.dto.response.ApiResponse;
import com.example.TaskManagerDemo1.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;
    @PostMapping("/log-in")
    public ApiResponse<String> login(@Valid  @RequestBody UserLoginRequest request) {
        return authenticationService.authenticate(request);
    }
}
