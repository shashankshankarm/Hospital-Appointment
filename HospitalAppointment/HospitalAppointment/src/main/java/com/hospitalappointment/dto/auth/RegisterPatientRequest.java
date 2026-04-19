package com.hospitalappointment.dto.auth;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegisterPatientRequest(
        @NotBlank @Size(max = 100) String username,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotNull LocalDate dateOfBirth,
        @NotBlank @Size(max = 20) String gender,
        @NotBlank @Size(max = 20) String phone,
        @Size(max = 500) String address
) {
}
