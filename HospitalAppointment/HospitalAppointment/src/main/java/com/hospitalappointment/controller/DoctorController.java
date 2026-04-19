package com.hospitalappointment.controller;

import com.hospitalappointment.dto.doctor.DoctorRequest;
import com.hospitalappointment.dto.doctor.DoctorResponse;
import com.hospitalappointment.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public DoctorResponse create(@RequestBody @Valid DoctorRequest request, Authentication authentication) {
        return doctorService.create(request, authentication.getName());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR')")
    public DoctorResponse get(@PathVariable Long id) {
        return doctorService.get(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','PATIENT')")
    public List<DoctorResponse> list(@RequestParam(required = false) String specialty) {
        return doctorService.list(specialty);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DoctorResponse update(@PathVariable Long id, @RequestBody @Valid DoctorRequest request, Authentication authentication) {
        return doctorService.update(id, request, authentication.getName());
    }
}
