package com.example.TaskManagerDemo1.security;

import com.example.TaskManagerDemo1.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurityService {

    final UserRepository userRepository;

    public boolean isUserSelf(Integer userId, Authentication authentication) {
        return userRepository.findById(userId)
                .map(user ->
                        user.getUsername().equals(authentication.getName())
                )
                .orElse(false);
    }
}

