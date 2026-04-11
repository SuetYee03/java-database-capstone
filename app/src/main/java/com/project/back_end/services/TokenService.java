package com.project.back_end.services;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String secret;

    public TokenService(AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    public String generateToken(String identifier) {
        return Jwts.builder()
                .subject(identifier)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractIdentifier(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean validateToken(String token, String user) {
        try {
            String identifier = extractIdentifier(token);

            if (identifier == null || identifier.isBlank()) {
                return false;
            }

            switch (user.toLowerCase()) {
                case "admin":
                    try {
                        Long adminId = Long.parseLong(identifier);
                        return adminRepository.findById(adminId).isPresent() && !isTokenExpired(token);
                    } catch (NumberFormatException e) {
                        // fallback: try username
                        Optional<Admin> adminOpt = adminRepository.findByUsername(identifier);
                        return adminOpt.isPresent() && !isTokenExpired(token);
                    }

                case "doctor":
                    Optional<Doctor> doctorOpt = Optional.ofNullable(doctorRepository.findByEmail(identifier));
                    return doctorOpt.isPresent() && !isTokenExpired(token);

                case "patient":
                    Optional<Patient> patientOpt = patientRepository.findByEmail(identifier);
                    return patientOpt.isPresent() && !isTokenExpired(token);

                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {
        return extractIdentifier(token);
    }

    public Long extractId(String token) {
        try {
            String identifier = extractIdentifier(token);
            return Long.parseLong(identifier);
        } catch (Exception e) {
            return null;
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes;

        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (Exception e) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
