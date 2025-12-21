package com.example.TaskManagerDemo1.configure;

import com.example.TaskManagerDemo1.entity.Users;
import com.example.TaskManagerDemo1.enums.Role;
import com.example.TaskManagerDemo1.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;
    UserRepository userRepository;

    @Bean
    ApplicationRunner initAdmin() {
        return args -> {
            if (userRepository.findByUsername("admin").isPresent()) {
                return;
            }

            Users admin = Users.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .roles(Set.of(Role.ADMIN.name()))
                    .build();

            userRepository.save(admin);
            log.warn("Admin account created");
        };
    }
}
