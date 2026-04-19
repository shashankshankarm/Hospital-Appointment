package com.hospitalappointment.service;

import com.hospitalappointment.entity.Role;
import com.hospitalappointment.entity.User;
import com.hospitalappointment.enums.RoleName;
import com.hospitalappointment.repository.RoleRepository;
import com.hospitalappointment.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.Set;

@Component
@ConditionalOnProperty(prefix = "app.security.jwt", name = "secret")
public class BootstrapService implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(BootstrapService.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BootstrapProperties bootstrapProperties;

    public BootstrapService(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder,
                            BootstrapProperties bootstrapProperties) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.bootstrapProperties = bootstrapProperties;
    }

    @Override
    public void run(String... args) {
        EnumSet.allOf(RoleName.class).forEach(roleName -> roleRepository.findByName(roleName).orElseGet(() -> {
            Role role = new Role();
            role.setName(roleName);
            return roleRepository.save(role);
        }));

        if (!StringUtils.hasText(bootstrapProperties.adminPassword())) {
            log.warn("BOOTSTRAP_ADMIN_PASSWORD is not set; skipping default admin user creation.");
            return;
        }

        if (!userRepository.existsByUsername(bootstrapProperties.adminUsername())) {
            Role adminRole = roleRepository.findByName(RoleName.ADMIN).orElseThrow();
            User admin = new User();
            admin.setUsername(bootstrapProperties.adminUsername());
            admin.setEmail(bootstrapProperties.adminEmail());
            admin.setPasswordHash(passwordEncoder.encode(bootstrapProperties.adminPassword()));
            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);
        }
    }
}
