package com.hospitalappointment.repository;

import com.hospitalappointment.entity.Appointment;
import com.hospitalappointment.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByDoctorIdAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
            Long doctorId,
            Collection<AppointmentStatus> statuses,
            LocalDateTime endTime,
            LocalDateTime startTime
    );

    List<Appointment> findByDoctorIdAndStartTimeBetweenOrderByStartTimeAsc(Long doctorId, LocalDateTime from, LocalDateTime to);
}
