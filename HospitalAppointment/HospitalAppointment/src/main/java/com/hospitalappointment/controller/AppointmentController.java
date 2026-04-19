package com.hospitalappointment.controller;

import com.hospitalappointment.dto.appointment.AppointmentRequest;
import com.hospitalappointment.dto.appointment.AppointmentResponse;
import com.hospitalappointment.dto.appointment.RescheduleRequest;
import com.hospitalappointment.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','PATIENT')")
    public ResponseEntity<AppointmentResponse> create(@RequestBody @Valid AppointmentRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.create(request, authentication.getName()));
    }

    @PatchMapping("/{id}/reschedule")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','PATIENT')")
    public AppointmentResponse reschedule(@PathVariable Long id, @RequestBody @Valid RescheduleRequest request,
                                          Authentication authentication) {
        return appointmentService.reschedule(id, request, authentication.getName());
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','PATIENT')")
    public AppointmentResponse cancel(@PathVariable Long id, Authentication authentication) {
        return appointmentService.cancel(id, authentication.getName());
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR')")
    public List<AppointmentResponse> doctorSchedule(@PathVariable Long doctorId,
                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return appointmentService.doctorSchedule(doctorId, date);
    }
}
