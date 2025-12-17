package com.example.TaskManagerDemo1.security;

import com.example.TaskManagerDemo1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurityService {

    @Autowired
    private UserRepository userRepository;

    public boolean isUserSelf(Integer userId, Authentication authentication) {
        return userRepository.findById(userId)
                .map(user ->
                        user.getUsername()
                                .equals(authentication.getName())
                )
                .orElse(false);
    }
}
