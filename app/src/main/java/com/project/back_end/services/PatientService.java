package com.project.back_end.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = tokenService.extractEmail(token);
            Optional<Patient> patientOpt = patientRepository.findByEmail(email);

            if (patientOpt.isEmpty()) {
                response.put("message", "Patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            Patient patient = patientOpt.get();

            if (!patient.getId().equals(id)) {
                response.put("message", "Unauthorized access");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            List<Appointment> appointments = appointmentRepository.findByPatient_Id(id);

            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            response.put("appointments", appointmentDTOs);
            response.put("message", "Appointments retrieved successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Failed to retrieve appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            int status;

            if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else if ("future".equalsIgnoreCase(condition)) {
                status = 0;
            } else {
                response.put("message", "Invalid condition");
                return ResponseEntity.badRequest().body(response);
            }

            List<Appointment> appointments =
                    appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(id, status);

            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            response.put("appointments", appointmentDTOs);
            response.put("message", "Filtered appointments retrieved successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Failed to filter appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Appointment> appointments =
                    appointmentRepository.filterByDoctorNameAndPatientId(name, patientId);

            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            response.put("appointments", appointmentDTOs);
            response.put("message", "Appointments filtered by doctor successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Failed to filter appointments by doctor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        Map<String, Object> response = new HashMap<>();

        try {
            int status;

            if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else if ("future".equalsIgnoreCase(condition)) {
                status = 0;
            } else {
                response.put("message", "Invalid condition");
                return ResponseEntity.badRequest().body(response);
            }

            List<Appointment> appointments =
                    appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(name, patientId, status);

            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            response.put("appointments", appointmentDTOs);
            response.put("message", "Appointments filtered by doctor and condition successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Failed to filter appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = tokenService.extractEmail(token);
            Optional<Patient> patientOpt = patientRepository.findByEmail(email);

            if (patientOpt.isEmpty()) {
                response.put("message", "Patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Patient patient = patientOpt.get();

            response.put("patient", patient);
            response.put("message", "Patient details retrieved successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Failed to retrieve patient details");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private AppointmentDTO convertToDTO(Appointment appointment) {
        return new AppointmentDTO(
                appointment.getId(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getName(),
                appointment.getPatient().getId(),
                appointment.getPatient().getName(),
                appointment.getPatient().getEmail(),
                appointment.getPatient().getPhone(),
                appointment.getPatient().getAddress(),
                appointment.getAppointmentTime(),
                appointment.getStatus()
        );
    }
}