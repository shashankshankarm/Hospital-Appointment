package com.hospitalappointment.dto.doctor;

public record DoctorResponse(
        Long id,
        String firstName,
        String lastName,
        String specialty,
        boolean available
) {
}
