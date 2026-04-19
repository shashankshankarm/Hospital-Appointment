package com.hospitalappointment.service;

import com.hospitalappointment.entity.Role;
import com.hospitalappointment.entity.User;
import com.hospitalappointment.enums.RoleName;
import com.hospitalappointment.repository.RoleRepository;
import com.hospitalappointment.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

@Component
public class BootstrapService implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public BootstrapService(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        EnumSet.allOf(RoleName.class).forEach(roleName -> roleRepository.findByName(roleName).orElseGet(() -> {
            Role role = new Role();
            role.setName(roleName);
            return roleRepository.save(role);
        }));

        if (!userRepository.existsByUsername("admin")) {
            Role adminRole = roleRepository.findByName(RoleName.ADMIN).orElseThrow();
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@hospital.local");
            admin.setPasswordHash(passwordEncoder.encode("Admin@123"));
            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);
        }
    }
}
