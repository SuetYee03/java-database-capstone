package com.project.back_end.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    @NotNull(message = "Doctor cannot be null")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id" , nullable = false)
    @NotNull(message = "Patient cannot be null")
    private Patient patient;

    @NotNull(message = "Appointment time cannot be null")
    private LocalDateTime appointmentTime;
    private int status; // 0 = Scheduled, 1 = Completed

    public Appointment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Transient
    public LocalDateTime getEndTime() {
        return appointmentTime != null ? appointmentTime.plusHours(1) : null;
    }

    @Transient
    public LocalDate getAppointmentDate() {
        return appointmentTime != null ? appointmentTime.toLocalDate() : null;
    }

    @Transient
    public LocalTime getAppointmentTimeOnly() {
        return appointmentTime != null ? appointmentTime.toLocalTime() : null;
    }
}