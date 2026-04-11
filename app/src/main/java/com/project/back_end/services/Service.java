package com.project.back_end.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.DTO.Login;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

@org.springframework.stereotype.Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public Service(TokenService tokenService,
                       AdminRepository adminRepository,
                       DoctorRepository doctorRepository,
                       PatientRepository patientRepository,
                       DoctorService doctorService,
                       PatientService patientService) {

        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    // ===============================
    // 1. Validate Token
    // ===============================
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {

        Map<String, String> response = new HashMap<>();

        if (!tokenService.validateToken(token, user)) {
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        response.put("message", "Token is valid");
        return ResponseEntity.ok(response);
    }

    // ===============================
    // 2. Validate Admin Login
    // ===============================
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {

        Map<String, String> response = new HashMap<>();

        Optional<Admin> adminOpt = adminRepository.findByUsername(receivedAdmin.getUsername());

        if (adminOpt.isEmpty() || !adminOpt.get().getPassword().equals(receivedAdmin.getPassword())) {
            response.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Admin admin = adminOpt.get();
        String token = tokenService.generateToken(String.valueOf(admin.getId()));
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    // ===============================
    // 3. Filter Doctor
    // ===============================
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {

        if (name != null && specialty != null && time != null) {
            return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        } else if (name != null && specialty != null) {
            return doctorService.filterDoctorByNameAndSpecility(name, specialty);
        } else if (name != null && time != null) {
            return doctorService.filterDoctorByNameAndTime(name, time);
        } else if (specialty != null && time != null) {
            return doctorService.filterDoctorByTimeAndSpecility(specialty, time);
        } else if (name != null) {
            return doctorService.findDoctorByName(name);
        } else if (specialty != null) {
            return doctorService.filterDoctorBySpecility(specialty);
        } else if (time != null) {
            return doctorService.filterDoctorsByTime(time);
        } else {
            return Map.of("doctors", doctorService.getDoctors());
        }
    }

    // ===============================
    // 4. Validate Appointment
    // ===============================
    public int validateAppointment(Appointment appointment) {

        Optional<Doctor> optionalDoctor = doctorRepository.findById(appointment.getDoctor().getId());

        if (optionalDoctor.isEmpty()) return -1;

        Doctor doctor = optionalDoctor.get();

        var availableSlots = doctorService.getDoctorAvailability(
                doctor.getId(),
                appointment.getAppointmentTime().toLocalDate()
        );

        String requestedTime = appointment.getAppointmentTime()
                .toLocalTime()
                .toString();

        if (availableSlots.contains(requestedTime)) {
            return 1;
        }

        return 0;
    }

    // ===============================
    // 5. Validate Patient (Register)
    // ===============================
    public boolean validatePatient(Patient patient) {

        Optional<Patient> existingOpt = patientRepository
                .findByEmailOrPhone(patient.getEmail(), patient.getPhone());

        return existingOpt.isEmpty();
    }

    // ===============================
    // 6. Validate Patient Login
    // ===============================
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {

        Map<String, String> response = new HashMap<>();

        Optional<Patient> patientOpt = patientRepository.findByEmail(login.getEmail());

        if (patientOpt.isEmpty() || !patientOpt.get().getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Patient patient = patientOpt.get();
        String token = tokenService.generateToken(String.valueOf(patient.getId()));
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    // ===============================
    // 7. Filter Patient Appointments
    // ===============================
    public ResponseEntity<Map<String, Object>> filterPatient(
            String condition,
            String name,
            String token
    ) {

        try {
            Long patientId = tokenService.extractId(token);
            Optional<Patient> patientOpt = patientRepository.findById(patientId);

            if (patientOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Patient not found"));
            }
            Patient patient = patientOpt.get();

            if (condition != null && name != null) {
                return patientService.filterByDoctorAndCondition(condition, name, patientId);
            } else if (condition != null) {
                return patientService.filterByCondition(condition, patientId);
            } else if (name != null) {
                return patientService.filterByDoctor(name, patientId);
            } else {
                return patientService.getPatientAppointment(patientId, token);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Filtering failed"));
        }
    }
}