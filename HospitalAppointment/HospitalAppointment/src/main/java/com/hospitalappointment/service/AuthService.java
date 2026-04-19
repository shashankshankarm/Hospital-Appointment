package com.hospitalappointment.service;

import com.hospitalappointment.common.exception.BadRequestException;
import com.hospitalappointment.common.exception.NotFoundException;
import com.hospitalappointment.dto.auth.*;
import com.hospitalappointment.entity.Patient;
import com.hospitalappointment.entity.RefreshToken;
import com.hospitalappointment.entity.Role;
import com.hospitalappointment.entity.User;
import com.hospitalappointment.enums.RoleName;
import com.hospitalappointment.repository.*;
import com.hospitalappointment.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PatientRepository patientRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuditLogService auditLogService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PatientRepository patientRepository,
                       RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService, AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.patientRepository = patientRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public AuthResponse registerPatient(RegisterPatientRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BadRequestException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.username().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        Role patientRole = roleRepository.findByName(RoleName.PATIENT)
                .orElseThrow(() -> new NotFoundException("PATIENT role not configured"));
        user.setRoles(Set.of(patientRole));
        user = userRepository.save(user);

        Patient patient = new Patient();
        patient.setMrn(generateMrn(user.getId()));
        patient.setFirstName(request.firstName().trim());
        patient.setLastName(request.lastName().trim());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setGender(request.gender().trim());
        patient.setPhone(request.phone().trim());
        patient.setEmail(request.email().trim().toLowerCase());
        patient.setAddress(request.address());
        patient.setUser(user);
        patientRepository.save(patient);

        auditLogService.log("PATIENT_REGISTERED", user.getUsername(), "PATIENT", patient.getId().toString(), "Self registration");

        return issueTokens(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new NotFoundException("User not found"));

        auditLogService.log("LOGIN", user.getUsername(), "USER", user.getId().toString(), "Successful login");

        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Refresh token expired or revoked");
        }

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        return issueTokens(refreshToken.getUser());
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshTokenValue = UUID.randomUUID() + "." + jwtService.generateRefreshToken(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(
                accessToken,
                refreshTokenValue,
                "Bearer",
                jwtService.accessTokenExpirySeconds(),
                user.getUsername(),
                user.getRoles().stream().map(role -> role.getName().name()).toList()
        );
    }

    private String generateMrn(Long userId) {
        return "MRN" + String.format("%08d", userId);
    }
}
