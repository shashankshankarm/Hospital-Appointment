package com.hospitalappointment.dto.patient;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PatientRequest(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotNull LocalDate dateOfBirth,
        @NotBlank @Size(max = 20) String gender,
        @NotBlank @Size(max = 20) String phone,
        @Email String email,
        @Size(max = 500) String address
) {
}
