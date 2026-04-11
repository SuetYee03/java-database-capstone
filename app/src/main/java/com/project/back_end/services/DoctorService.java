package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.back_end.models.Doctor;
import com.project.back_end.models.Appointment;
import com.project.back_end.DTO.Login;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.AppointmentRepository;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    // ===============================
    // 1. Get Availability
    // ===============================
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {

        Optional<Doctor> optionalDoctor = doctorRepository.findById(doctorId);
        if (optionalDoctor.isEmpty()) return List.of();

        Doctor doctor = optionalDoctor.get();

        List<String> allSlots = doctor.getAvailableTimes(); // assume exists

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Appointment> booked = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);

        List<String> bookedSlots = booked.stream()
                .map(a -> a.getAppointmentTime().toLocalTime().toString())
                .collect(Collectors.toList());

        return allSlots.stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());
    }

    // ===============================
    // 2. Save Doctor
    // ===============================
    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // ===============================
    // 3. Update Doctor
    // ===============================
    public int updateDoctor(Doctor doctor) {
        try {
            if (!doctorRepository.existsById(doctor.getId())) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // ===============================
    // 4. Get All Doctors
    // ===============================
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    // ===============================
    // 5. Delete Doctor
    // ===============================
    public int deleteDoctor(long id) {
        try {
            if (!doctorRepository.existsById(id)) return -1;

            appointmentRepository.deleteAllByDoctor_Id(id);
            doctorRepository.deleteById(id);

            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // ===============================
    // 6. Validate Doctor Login
    // ===============================
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {

        Map<String, String> response = new HashMap<>();

        Doctor doctor = doctorRepository.findByEmail(login.getEmail());

        if (doctor == null || !doctor.getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid credentials");
            return ResponseEntity.badRequest().body(response);
        }

        String token = tokenService.generateToken(String.valueOf(doctor.getId()));

        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    // ===============================
    // 7. Find Doctor by Name
    // ===============================
    public Map<String, Object> findDoctorByName(String name) {

        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCase(name);

        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);

        return result;
    }

    // ===============================
    // 8. Filter Methods
    // ===============================
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {

        List<Doctor> doctors = doctorRepository
                .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);

        doctors = filterDoctorByTime(doctors, amOrPm);

        return Map.of("doctors", doctors);
    }

    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {

        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCase(name);

        doctors = filterDoctorByTime(doctors, amOrPm);

        return Map.of("doctors", doctors);
    }

    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {

        List<Doctor> doctors = doctorRepository
                .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);

        return Map.of("doctors", doctors);
    }

    public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String amOrPm) {

        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);

        doctors = filterDoctorByTime(doctors, amOrPm);

        return Map.of("doctors", doctors);
    }

    public Map<String, Object> filterDoctorBySpecility(String specialty) {

        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);

        return Map.of("doctors", doctors);
    }

    public Map<String, Object> filterDoctorsByTime(String amOrPm) {

        List<Doctor> doctors = doctorRepository.findAll();

        doctors = filterDoctorByTime(doctors, amOrPm);

        return Map.of("doctors", doctors);
    }

    // ===============================
    // PRIVATE FILTER
    // ===============================
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {

        if (amOrPm == null) return doctors;

        return doctors.stream()
                .filter(doc -> doc.getAvailableTimes().stream().anyMatch(time ->
                        amOrPm.equalsIgnoreCase("AM") ? time.compareTo("12:00") < 0 : time.compareTo("12:00") >= 0
                ))
                .collect(Collectors.toList());
    }
}
