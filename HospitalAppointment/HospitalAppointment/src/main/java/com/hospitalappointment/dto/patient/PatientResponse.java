package com.hospitalappointment.dto.patient;

import java.time.LocalDate;

public record PatientResponse(
        Long id,
        String mrn,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String gender,
        String phone,
        String email,
        String address
) {
}
