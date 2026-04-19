package com.hospitalappointment.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Service
public class MrnGeneratorService {

    public String generate(LocalDate dateOfBirth) {
        String dob = Objects.requireNonNull(dateOfBirth, "dateOfBirth is required for MRN generation")
                .toString()
                .replace("-", "");
        return "MRN-" + dob + "-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase().replace("-", "");
    }
}
