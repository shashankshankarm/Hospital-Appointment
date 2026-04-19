package com.hospitalappointment.service;

import com.hospitalappointment.common.exception.ConflictException;
import com.hospitalappointment.common.exception.NotFoundException;
import com.hospitalappointment.dto.appointment.AppointmentRequest;
import com.hospitalappointment.dto.appointment.AppointmentResponse;
import com.hospitalappointment.dto.appointment.RescheduleRequest;
import com.hospitalappointment.entity.Appointment;
import com.hospitalappointment.entity.Doctor;
import com.hospitalappointment.entity.Patient;
import com.hospitalappointment.enums.AppointmentStatus;
import com.hospitalappointment.repository.AppointmentRepository;
import com.hospitalappointment.repository.DoctorRepository;
import com.hospitalappointment.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

@Service
public class AppointmentService {

    private static final EnumSet<AppointmentStatus> ACTIVE_STATUSES = EnumSet.of(
            AppointmentStatus.BOOKED,
            AppointmentStatus.CHECKED_IN,
            AppointmentStatus.IN_PROGRESS
    );

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AuditLogService auditLogService;

    public AppointmentService(AppointmentRepository appointmentRepository, DoctorRepository doctorRepository,
                              PatientRepository patientRepository, AuditLogService auditLogService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public AppointmentResponse create(AppointmentRequest request, String actor) {
        validateRange(request.startTime(), request.endTime());

        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new NotFoundException("Patient not found"));
        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new NotFoundException("Doctor not found"));

        ensureNoConflict(doctor.getId(), request.startTime(), request.endTime());

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setStartTime(request.startTime());
        appointment.setEndTime(request.endTime());
        appointment.setReason(request.reason());
        appointment.setStatus(AppointmentStatus.BOOKED);
        appointment.setCreatedBy(actor);

        appointment = appointmentRepository.save(appointment);
        auditLogService.log("APPOINTMENT_CREATED", actor, "APPOINTMENT", appointment.getId().toString(), "Appointment booked");

        return mapResponse(appointment);
    }

    @Transactional
    public AppointmentResponse reschedule(Long appointmentId, RescheduleRequest request, String actor) {
        validateRange(request.startTime(), request.endTime());

        Appointment appointment = getEntity(appointmentId);
        if (appointment.getStatus() == AppointmentStatus.CANCELLED || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new ConflictException("Cannot reschedule a completed/cancelled appointment");
        }

        ensureNoConflict(appointment.getDoctor().getId(), request.startTime(), request.endTime());
        appointment.setStartTime(request.startTime());
        appointment.setEndTime(request.endTime());
        appointment = appointmentRepository.save(appointment);

        auditLogService.log("APPOINTMENT_RESCHEDULED", actor, "APPOINTMENT", appointment.getId().toString(), "Appointment rescheduled");
        return mapResponse(appointment);
    }

    @Transactional
    public AppointmentResponse cancel(Long appointmentId, String actor) {
        Appointment appointment = getEntity(appointmentId);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment = appointmentRepository.save(appointment);

        auditLogService.log("APPOINTMENT_CANCELLED", actor, "APPOINTMENT", appointment.getId().toString(), "Appointment cancelled");
        return mapResponse(appointment);
    }

    public List<AppointmentResponse> doctorSchedule(Long doctorId, LocalDate date) {
        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.plusDays(1).atStartOfDay();

        return appointmentRepository.findByDoctorIdAndStartTimeBetweenOrderByStartTimeAsc(doctorId, from, to)
                .stream()
                .map(this::mapResponse)
                .toList();
    }

    private Appointment getEntity(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));
    }

    private void ensureNoConflict(Long doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        boolean conflict = appointmentRepository.existsByDoctorIdAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
                doctorId,
                ACTIVE_STATUSES,
                endTime,
                startTime
        );
        if (conflict) {
            throw new ConflictException("Requested slot conflicts with existing appointment");
        }
    }

    private void validateRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new ConflictException("End time must be after start time");
        }
    }

    private AppointmentResponse mapResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus(),
                appointment.getReason(),
                appointment.getCreatedBy()
        );
    }
}
