package com.hospitalappointment.controller;

import com.hospitalappointment.dto.patient.PatientRequest;
import com.hospitalappointment.dto.patient.PatientResponse;
import com.hospitalappointment.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public PatientResponse create(@RequestBody @Valid PatientRequest request, Authentication authentication) {
        return patientService.create(request, authentication.getName());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR')")
    public PatientResponse get(@PathVariable Long id) {
        return patientService.get(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR')")
    public List<PatientResponse> search(@RequestParam(required = false) String query) {
        return patientService.search(query);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public PatientResponse update(@PathVariable Long id, @RequestBody @Valid PatientRequest request, Authentication authentication) {
        return patientService.update(id, request, authentication.getName());
    }
}
