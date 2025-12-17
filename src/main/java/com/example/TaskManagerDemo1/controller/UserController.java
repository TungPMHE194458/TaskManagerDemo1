package com.example.TaskManagerDemo1.controller;

import com.example.TaskManagerDemo1.dto.response.ApiResponse;
import com.example.TaskManagerDemo1.dto.request.UserAddRequest;
import com.example.TaskManagerDemo1.dto.request.UserUpdateRequest;
import com.example.TaskManagerDemo1.entity.Users;
import com.example.TaskManagerDemo1.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;



    @PostMapping
    public ApiResponse<Users> addUser(@Valid  @RequestBody UserAddRequest request){

        return userService.addUsers(request);
    }

    @GetMapping("/{userId}")
    public ApiResponse<Users> getUser(@PathVariable("userId") String userId){
        return userService.getUserById(Integer.parseInt(userId));
    }

    @GetMapping
    public ApiResponse<List<Users>> getAllUsers(){
        return userService.getAllUsers();
    }


    @PutMapping("/{userId}")
    public ApiResponse<Users> updateUser(@PathVariable("userId") int userId, @RequestBody UserUpdateRequest request){
        return userService.updateUsers(userId, request);
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<String> deleteUser(@PathVariable("userId") String userId){
        return userService.deleteUserById(Integer.parseInt(userId));
    }


}
