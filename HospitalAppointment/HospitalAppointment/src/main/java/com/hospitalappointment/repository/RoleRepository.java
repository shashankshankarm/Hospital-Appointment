package com.hospitalappointment.repository;

import com.hospitalappointment.entity.Role;
import com.hospitalappointment.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
