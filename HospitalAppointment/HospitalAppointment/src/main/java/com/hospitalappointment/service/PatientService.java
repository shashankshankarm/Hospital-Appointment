package com.hospitalappointment.service;

import com.hospitalappointment.common.exception.BadRequestException;
import com.hospitalappointment.common.exception.NotFoundException;
import com.hospitalappointment.dto.patient.PatientRequest;
import com.hospitalappointment.dto.patient.PatientResponse;
import com.hospitalappointment.entity.Patient;
import com.hospitalappointment.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AuditLogService auditLogService;

    public PatientService(PatientRepository patientRepository, AuditLogService auditLogService) {
        this.patientRepository = patientRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public PatientResponse create(PatientRequest request, String actor) {
        if (patientRepository.existsByFirstNameAndLastNameAndDateOfBirth(
                request.firstName(), request.lastName(), request.dateOfBirth())
                || patientRepository.existsByPhoneAndDateOfBirth(request.phone(), request.dateOfBirth())) {
            throw new BadRequestException("Potential duplicate patient record detected");
        }

        Patient patient = new Patient();
        map(request, patient);
        patient.setMrn(generateMrn(request));
        patient = patientRepository.save(patient);
        auditLogService.log("PATIENT_CREATED", actor, "PATIENT", patient.getId().toString(), "Patient created");
        return mapResponse(patient);
    }

    public PatientResponse get(Long id) {
        return mapResponse(getEntity(id));
    }

    public List<PatientResponse> search(String query) {
        String keyword = query == null ? "" : query.trim();
        return patientRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrMrnContainingIgnoreCase(keyword, keyword, keyword)
                .stream()
                .map(this::mapResponse)
                .toList();
    }

    @Transactional
    public PatientResponse update(Long id, PatientRequest request, String actor) {
        Patient patient = getEntity(id);
        map(request, patient);
        patient = patientRepository.save(patient);
        auditLogService.log("PATIENT_UPDATED", actor, "PATIENT", patient.getId().toString(), "Patient updated");
        return mapResponse(patient);
    }

    private Patient getEntity(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Patient not found"));
    }

    private void map(PatientRequest request, Patient patient) {
        patient.setFirstName(request.firstName().trim());
        patient.setLastName(request.lastName().trim());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setGender(request.gender().trim());
        patient.setPhone(request.phone().trim());
        patient.setEmail(request.email() == null ? null : request.email().trim().toLowerCase());
        patient.setAddress(request.address());
    }

    private String generateMrn(PatientRequest request) {
        return "MRN-" + request.dateOfBirth().toString().replace("-", "") + "-" + (System.nanoTime() % 100000);
    }

    private PatientResponse mapResponse(Patient patient) {
        return new PatientResponse(
                patient.getId(),
                patient.getMrn(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getPhone(),
                patient.getEmail(),
                patient.getAddress()
        );
    }
}
