package com.hospitalappointment.service;

import com.hospitalappointment.common.exception.NotFoundException;
import com.hospitalappointment.dto.doctor.DoctorRequest;
import com.hospitalappointment.dto.doctor.DoctorResponse;
import com.hospitalappointment.entity.Doctor;
import com.hospitalappointment.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AuditLogService auditLogService;

    public DoctorService(DoctorRepository doctorRepository, AuditLogService auditLogService) {
        this.doctorRepository = doctorRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public DoctorResponse create(DoctorRequest request, String actor) {
        Doctor doctor = new Doctor();
        map(request, doctor);
        doctor = doctorRepository.save(doctor);
        auditLogService.log("DOCTOR_CREATED", actor, "DOCTOR", doctor.getId().toString(), "Doctor profile created");
        return mapResponse(doctor);
    }

    public DoctorResponse get(Long id) {
        return mapResponse(getEntity(id));
    }

    public List<DoctorResponse> list(String specialty) {
        if (specialty == null || specialty.isBlank()) {
            return doctorRepository.findAll().stream().map(this::mapResponse).toList();
        }
        return doctorRepository.findBySpecialtyContainingIgnoreCase(specialty).stream().map(this::mapResponse).toList();
    }

    @Transactional
    public DoctorResponse update(Long id, DoctorRequest request, String actor) {
        Doctor doctor = getEntity(id);
        map(request, doctor);
        doctor = doctorRepository.save(doctor);
        auditLogService.log("DOCTOR_UPDATED", actor, "DOCTOR", doctor.getId().toString(), "Doctor profile updated");
        return mapResponse(doctor);
    }

    private Doctor getEntity(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Doctor not found"));
    }

    private void map(DoctorRequest request, Doctor doctor) {
        doctor.setFirstName(request.firstName().trim());
        doctor.setLastName(request.lastName().trim());
        doctor.setSpecialty(request.specialty().trim());
        doctor.setAvailable(request.available());
    }

    private DoctorResponse mapResponse(Doctor doctor) {
        return new DoctorResponse(
                doctor.getId(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getSpecialty(),
                doctor.isAvailable()
        );
    }
}
