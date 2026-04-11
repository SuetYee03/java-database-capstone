package com.project.back_end.controllers;

import java.time.LocalDate;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    @GetMapping("/{date}/{pname}/{token}")
    public ResponseEntity<?> getAppointments(@PathVariable String date,
                                             @PathVariable String pname,
                                             @PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "doctor");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }
        return ResponseEntity.ok(appointmentService.getAppointment(pname, LocalDate.parse(date), token));
    }

    @GetMapping("/all/{token}")
    public ResponseEntity<?> getDoctorAllAppointments(@PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "doctor");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }
        return ResponseEntity.ok(appointmentService.getAllAppointmentsForDoctor(null, token));
    }

    @GetMapping("/all/{pname}/{token}")
    public ResponseEntity<?> getDoctorAllAppointmentsWithPatient(@PathVariable String pname,
                                                                 @PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "doctor");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }
        return ResponseEntity.ok(appointmentService.getAllAppointmentsForDoctor(pname, token));
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(@RequestBody Appointment appointment,
                                                               @PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        int validationResult = service.validateAppointment(appointment);
        if (validationResult == -1) {
            return ResponseEntity.badRequest().body(Map.of("message", "Doctor not found"));
        }
        if (validationResult == 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "Appointment time is not available"));
        }

        int result = appointmentService.bookAppointment(appointment);
        if (result == 1) {
            return ResponseEntity.ok(Map.of("message", "Appointment booked successfully"));
        }
        return ResponseEntity.internalServerError().body(Map.of("message", "Failed to book appointment"));
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(@RequestBody Appointment appointment,
                                                                 @PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }
        return appointmentService.updateAppointment(appointment);
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(@PathVariable long id,
                                                                 @PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }
        return appointmentService.cancelAppointment(id, token);
    }
}