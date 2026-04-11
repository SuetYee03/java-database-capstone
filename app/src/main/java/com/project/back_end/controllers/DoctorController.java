package com.project.back_end.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final Service service;

    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(@PathVariable String user,
                                                   @PathVariable Long doctorId,
                                                   @PathVariable String date,
                                                   @PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, user);
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        LocalDate localDate = LocalDate.parse(date);
        List<String> availability = doctorService.getDoctorAvailability(doctorId, localDate);

        Map<String, Object> response = new HashMap<>();
        response.put("availability", availability);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctors() {
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorService.getDoctors());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> addDoctor(@RequestBody Doctor doctor,
                                                         @PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "admin");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        int result = doctorService.saveDoctor(doctor);

        if (result == 1) {
            return ResponseEntity.ok(Map.of("message", "Doctor added to db"));
        } else if (result == -1) {
            return ResponseEntity.status(409).body(Map.of("message", "Doctor already exists"));
        } else {
            return ResponseEntity.internalServerError().body(Map.of("message", "Some internal error occurred"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(@RequestBody Doctor doctor,
                                                            @PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "admin");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        int result = doctorService.updateDoctor(doctor);

        if (result == 1) {
            return ResponseEntity.ok(Map.of("message", "Doctor updated"));
        } else if (result == -1) {
            return ResponseEntity.status(404).body(Map.of("message", "Doctor not found"));
        } else {
            return ResponseEntity.internalServerError().body(Map.of("message", "Some internal error occurred"));
        }
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(@PathVariable long id,
                                                            @PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "admin");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        int result = doctorService.deleteDoctor(id);

        if (result == 1) {
            return ResponseEntity.ok(Map.of("message", "Doctor deleted successfully"));
        } else if (result == -1) {
            return ResponseEntity.status(404).body(Map.of("message", "Doctor not found with id"));
        } else {
            return ResponseEntity.internalServerError().body(Map.of("message", "Some internal error occurred"));
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> filterDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String time,
            @RequestParam(required = false) String speciality) {
        return ResponseEntity.ok(service.filterDoctor(name, speciality, time));
    }
}
