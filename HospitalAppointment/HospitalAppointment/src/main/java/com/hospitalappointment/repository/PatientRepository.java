package com.hospitalappointment.repository;

import com.hospitalappointment.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByFirstNameAndLastNameAndDateOfBirth(String firstName, String lastName, LocalDate dateOfBirth);

    boolean existsByPhoneAndDateOfBirth(String phone, LocalDate dateOfBirth);

    List<Patient> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrMrnContainingIgnoreCase(
            String firstName, String lastName, String mrn
    );
}
