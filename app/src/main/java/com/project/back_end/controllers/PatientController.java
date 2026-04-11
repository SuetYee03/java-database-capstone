package com.project.back_end.controllers;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    public PatientController(PatientService patientService, Service service) {
        this.patientService = patientService;
        this.service = service;
    }

    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatientDetails(@PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(tokenValidation.getStatusCode())
                    .body(Map.of("message", tokenValidation.getBody().get("message")));
        }
        return patientService.getPatientDetails(token);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(@RequestBody Patient patient) {
        if (!service.validatePatient(patient)) {
            return ResponseEntity.status(409)
                    .body(Map.of("message", "Patient with email id or phone no already exist"));
        }

        int result = patientService.createPatient(patient);
        if (result == 1) {
            return ResponseEntity.ok(Map.of("message", "Signup successful"));
        }
        return ResponseEntity.internalServerError()
                .body(Map.of("message", "Internal server error"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> patientLogin(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    @GetMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointments(@PathVariable Long id,
                                                                      @PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(tokenValidation.getStatusCode())
                    .body(Map.of("message", tokenValidation.getBody().get("message")));
        }
        return patientService.getPatientAppointment(id, token);
    }

    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointments(@PathVariable String condition,
                                                                         @PathVariable String name,
                                                                         @PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(tokenValidation.getStatusCode())
                    .body(Map.of("message", tokenValidation.getBody().get("message")));
        }
        return service.filterPatient(condition, name, token);
    }
}
