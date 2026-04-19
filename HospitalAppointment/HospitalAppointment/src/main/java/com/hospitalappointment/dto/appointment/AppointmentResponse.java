package com.hospitalappointment.dto.appointment;

import com.hospitalappointment.enums.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long patientId,
        String patientName,
        Long doctorId,
        String doctorName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AppointmentStatus status,
        String reason,
        String createdBy
) {
}
