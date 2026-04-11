package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.project.back_end.DTO.AppointmentDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private com.project.back_end.services.Service appointmentService;

    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();

        try {
            Optional<Appointment> existingAppointment = appointmentRepository.findById(appointment.getId());

            if (existingAppointment.isEmpty()) {
                response.put("message", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            int validation = appointmentService.validateAppointment(appointment);

            if (validation != 1) {
                Map<String, String> error = new HashMap<>();
                error.put("message", validation == 0 ? "Time slot not available" : "Doctor not found");
                return ResponseEntity.badRequest().body(error);
            }

            appointmentRepository.save(appointment);
            response.put("message", "Appointment updated successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Failed to update appointment");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();

        try {
            Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);

            if (optionalAppointment.isEmpty()) {
                response.put("message", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Appointment appointment = optionalAppointment.get();

            Long patientIdFromToken = tokenService.extractId(token);

            if (patientIdFromToken == null) {
                response.put("message", "Invalid token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            if (appointment.getPatient() == null || !appointment.getPatient().getId().equals(patientIdFromToken)) {
                response.put("message", "You are not allowed to cancel this appointment");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            appointmentRepository.delete(appointment);
            response.put("message", "Appointment cancelled successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Failed to cancel appointment");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> result = new HashMap<>();

        try {
            Long doctorId = tokenService.extractId(token);

            if (doctorId == null) {
                result.put("message", "Invalid token");
                result.put("appointments", List.of());
                return result;
            }

            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay().minusSeconds(1);

            List<Appointment> appointments;

            if (pname != null && !pname.trim().isEmpty()) {
                appointments = appointmentRepository
                        .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                                doctorId, pname, start, end
                        );
            } else {
                appointments = appointmentRepository
                        .findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
            }

            List<AppointmentDTO> dtoList = appointments.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            result.put("appointments", dtoList);
            result.put("message", "Appointments retrieved successfully");
            return result;

        } catch (Exception e) {
            result.put("message", "Failed to retrieve appointments");
            result.put("appointments", List.of());
            return result;
        }
    }

    public Map<String, Object> getAllAppointmentsForDoctor(String pname, String token) {
        Map<String, Object> result = new HashMap<>();

        try {
            Long doctorId = tokenService.extractId(token);

            if (doctorId == null) {
                result.put("message", "Invalid token");
                result.put("appointments", List.of());
                return result;
            }

            List<Appointment> appointments;

            if (pname != null && !pname.trim().isEmpty() && !pname.equals("null")) {
                appointments = appointmentRepository
                        .findByDoctorIdAndPatient_NameContainingIgnoreCase(doctorId, pname);
            } else {
                appointments = appointmentRepository.findByDoctorId(doctorId);
            }

            List<AppointmentDTO> dtoList = appointments.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            result.put("appointments", dtoList);
            result.put("message", "All appointments retrieved successfully");
            return result;

        } catch (Exception e) {
            result.put("message", "Failed to retrieve all appointments");
            result.put("appointments", List.of());
            return result;
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
