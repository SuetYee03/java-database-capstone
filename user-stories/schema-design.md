# Schema Architecture

# MySQL Schema Design

## Database Name
cms

## Table 1: admin
| Field Name | Data Type | Constraints |
|------------|-----------|-------------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT |
| username | VARCHAR(50) | NOT NULL, UNIQUE |
| password | VARCHAR(255) | NOT NULL |

## Table 2: doctor
| Field Name | Data Type | Constraints |
|------------|-----------|-------------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT |
| name | VARCHAR(100) | NOT NULL |
| email | VARCHAR(100) | NOT NULL, UNIQUE |
| password | VARCHAR(255) | NOT NULL |
| phone | VARCHAR(20) | NOT NULL |
| specialty | VARCHAR(100) | NOT NULL |

## Table 3: patient
| Field Name | Data Type | Constraints |
|------------|-----------|-------------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT |
| name | VARCHAR(100) | NOT NULL |
| email | VARCHAR(100) | NOT NULL, UNIQUE |
| password | VARCHAR(255) | NOT NULL |
| phone | VARCHAR(20) | NOT NULL |
| address | VARCHAR(255) | NOT NULL |

## Table 4: appointment
| Field Name | Data Type | Constraints |
|------------|-----------|-------------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT |
| appointment_time | DATETIME | NOT NULL |
| status | INT | NOT NULL |
| doctor_id | INT | NOT NULL, FOREIGN KEY REFERENCES doctor(id) |
| patient_id | INT | NOT NULL, FOREIGN KEY REFERENCES patient(id) |

## Table 5: doctor_available_times
| Field Name | Data Type | Constraints |
|------------|-----------|-------------|
| doctor_id | INT | NOT NULL, FOREIGN KEY REFERENCES doctor(id) |
| available_times | VARCHAR(20) | NOT NULL |

## Relationships
- `appointment.doctor_id` references `doctor.id`
- `appointment.patient_id` references `patient.id`
- `doctor_available_times.doctor_id` references `doctor.id`

## Summary
This schema supports:
- admin authentication
- doctor management
- patient registration and login
- appointment booking
- doctor availability filtering
