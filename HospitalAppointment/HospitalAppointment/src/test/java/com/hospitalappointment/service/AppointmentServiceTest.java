package com.hospitalappointment.service;

import com.hospitalappointment.common.exception.ConflictException;
import com.hospitalappointment.dto.appointment.AppointmentRequest;
import com.hospitalappointment.entity.Doctor;
import com.hospitalappointment.entity.Patient;
import com.hospitalappointment.repository.AppointmentRepository;
import com.hospitalappointment.repository.DoctorRepository;
import com.hospitalappointment.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {
    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2030, 1, 1, 9, 0);

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AppointmentService appointmentService;

    private Doctor doctor;
    private Patient patient;

    @BeforeEach
    void setUp() {
        doctor = new Doctor();
        ReflectionTestUtils.setField(doctor, "id", 2L);
        doctor.setFirstName("Alex");
        doctor.setLastName("Grey");

        patient = new Patient();
        ReflectionTestUtils.setField(patient, "id", 1L);
        patient.setFirstName("Sam");
        patient.setLastName("Ray");
    }

    @Test
    void create_shouldThrowConflict_whenSlotAlreadyBooked() {
        LocalDateTime start = FIXED_TIME.plusDays(1);
        LocalDateTime end = start.plusMinutes(30);
        AppointmentRequest request = new AppointmentRequest(1L, 2L, start, end, "Consultation");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.existsByDoctorIdAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(anyLong(), any(), any(), any()))
                .thenReturn(true);

        assertThrows(ConflictException.class, () -> appointmentService.create(request, "tester"));
    }

    @Test
    void create_shouldThrowConflict_whenEndBeforeStart() {
        LocalDateTime start = FIXED_TIME.plusDays(1);
        LocalDateTime end = start.minusMinutes(5);
        AppointmentRequest request = new AppointmentRequest(1L, 2L, start, end, "Consultation");

        assertThrows(ConflictException.class, () -> appointmentService.create(request, "tester"));
    }
}
