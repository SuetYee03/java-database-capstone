package com.project.back_end.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final Service service;

    public PrescriptionController(PrescriptionService prescriptionService, Service service) {
        this.prescriptionService = prescriptionService;
        this.service = service;
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(@PathVariable String token,
                                                                @RequestBody Prescription prescription) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "doctor");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        return prescriptionService.savePrescription(prescription);
    }

    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescription(@PathVariable Long appointmentId,
                                                               @PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "doctor");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(tokenValidation.getStatusCode())
                    .body(Map.of("message", tokenValidation.getBody().get("message")));
        }

        return prescriptionService.getPrescription(appointmentId);
    }
}