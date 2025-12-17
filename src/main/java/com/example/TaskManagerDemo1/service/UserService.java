package com.example.TaskManagerDemo1.service;

import com.example.TaskManagerDemo1.dto.request.UserAddRequest;
import com.example.TaskManagerDemo1.dto.request.UserUpdateRequest;
import com.example.TaskManagerDemo1.dto.response.ApiResponse;
import com.example.TaskManagerDemo1.entity.Users;
import com.example.TaskManagerDemo1.enums.Role;
import com.example.TaskManagerDemo1.exception.AppException;
import com.example.TaskManagerDemo1.exception.ErrorCode;
import com.example.TaskManagerDemo1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * CREATE USER (PUBLIC)
     */
    public ApiResponse<Users> addUsers(UserAddRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }

        Users user = new Users();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
        user.setRoles(roles);

        return ApiResponse.success(userRepository.save(user));
    }

    /**
     * GET USER BY ID (SELF or ADMIN)
     */
    @PreAuthorize("@userSecurity.isUserSelf(#id, authentication) or hasRole('ADMIN')")
    public ApiResponse<Users> getUserById(int id) {

        Users user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return ApiResponse.success(user);
    }

    /**
     * GET ALL USERS (ADMIN)
     */
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Users>> getAllUsers() {
        return ApiResponse.success(userRepository.findAll());
    }

    /**
     * UPDATE USER (SELF or ADMIN)
     */
    @PreAuthorize("@userSecurity.isUserSelf(#userId, authentication) or hasRole('ADMIN')")
    public ApiResponse<Users> updateUsers(int userId, UserUpdateRequest request) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        return ApiResponse.success(userRepository.save(user));
    }

    /**
     * DELETE USER (ADMIN)
     */
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteUserById(int id) {

        Users user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userRepository.delete(user);
        return ApiResponse.success("User " + id + " has been deleted");
    }
}
