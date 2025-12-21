package com.example.TaskManagerDemo1.service;

import com.example.TaskManagerDemo1.dto.request.UserAddRequest;
import com.example.TaskManagerDemo1.dto.request.UserUpdateRequest;
import com.example.TaskManagerDemo1.dto.response.ApiResponse;
import com.example.TaskManagerDemo1.dto.response.UserResponse;
import com.example.TaskManagerDemo1.entity.Users;
import com.example.TaskManagerDemo1.enums.Role;
import com.example.TaskManagerDemo1.exception.AppException;
import com.example.TaskManagerDemo1.exception.ErrorCode;
import com.example.TaskManagerDemo1.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /* ===================== CREATE USER ===================== */
    public ApiResponse<UserResponse> addUsers(UserAddRequest request) {

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

        Users saved = userRepository.save(user);

        return ApiResponse.success(toUserResponse(saved));
    }

    /* ===================== GET USER BY ID ===================== */
    @PreAuthorize("@userSecurity.isUserSelf(#id, authentication) or hasRole('ADMIN')")
    public ApiResponse<UserResponse> getUserById(int id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return ApiResponse.success(toUserResponse(user));
    }

    /* ===================== GET ALL USERS ===================== */
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userRepository.findAll()
                .stream()
                .map(this::toUserResponse)
                .toList();
        return ApiResponse.success(users);
    }

    /* ===================== UPDATE USER ===================== */
    @PreAuthorize("@userSecurity.isUserSelf(#userId, authentication) or hasRole('ADMIN')")
    public ApiResponse<UserResponse> updateUsers(int userId, UserUpdateRequest request) {

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

        Users saved = userRepository.save(user);
        return ApiResponse.success(toUserResponse(saved));
    }

    /* ===================== DELETE USER ===================== */
    @Transactional
    public ApiResponse<String> deleteUser(int userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        userRepository.delete(user); // Hibernate tự xóa tất cả liên quan

        return  ApiResponse.success("User deleted");
    }


    /* ===================== MAPPER ===================== */
    private UserResponse toUserResponse(Users user) {
        UserResponse response = new UserResponse();
        response.setID(user.getID());
        response.setUsername(user.getUsername());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setRoles(user.getRoles());
        return response;
    }
}
