## MySQL Database Design

This clinic system needs the following core tables:
- patients
- doctors
- appointments
- admin
- doctor_availability
- payments

### Table: patients
- id: INT, Primary Key, Auto Increment
- full_name: VARCHAR(100), Not Null
- email: VARCHAR(100), Not Null, Unique
- phone: VARCHAR(20), Not Null, Unique
- password_hash: VARCHAR(255), Not Null
- date_of_birth: DATE, Null
- gender: VARCHAR(10), Null
- address: VARCHAR(255), Null
- created_at: DATETIME, Not Null
- updated_at: DATETIME, Not Null
- is_active: BOOLEAN, Not Null, Default TRUE

**Design Notes:**
- `email` and `phone` should be unique to avoid duplicate patient accounts.
- Email and phone format validation should be handled later in application code.
- `password_hash` is stored instead of plain password for security.
- `is_active` supports soft delete instead of permanently removing patients.

### Table: doctors
- id: INT, Primary Key, Auto Increment
- full_name: VARCHAR(100), Not Null
- email: VARCHAR(100), Not Null, Unique
- phone: VARCHAR(20), Not Null, Unique
- specialization: VARCHAR(100), Not Null
- license_number: VARCHAR(50), Not Null, Unique
- profile_description: TEXT, Null
- consultation_fee: DECIMAL(10,2), Not Null
- created_at: DATETIME, Not Null
- updated_at: DATETIME, Not Null
- is_active: BOOLEAN, Not Null, Default TRUE

**Design Notes:**
- `license_number` should be unique because each doctor should have one valid professional identity.
- `is_active` is better than hard delete because old appointments may still need to reference the doctor.
- Contact format validation can be handled in code later.

### Table: admin
- id: INT, Primary Key, Auto Increment
- username: VARCHAR(50), Not Null, Unique
- email: VARCHAR(100), Not Null, Unique
- password_hash: VARCHAR(255), Not Null
- role: VARCHAR(30), Not Null
- created_at: DATETIME, Not Null
- last_login_at: DATETIME, Null
- is_active: BOOLEAN, Not Null, Default TRUE

**Design Notes:**
- `username` and `email` must be unique.
- `role` allows future expansion such as super admin or support admin.
- `password_hash` improves security.

### Table: appointments
- id: INT, Primary Key, Auto Increment
- patient_id: INT, Foreign Key → patients(id), Not Null
- doctor_id: INT, Foreign Key → doctors(id), Not Null
- appointment_start: DATETIME, Not Null
- appointment_end: DATETIME, Not Null
- status: VARCHAR(20), Not Null
- reason: VARCHAR(255), Null
- created_at: DATETIME, Not Null
- updated_at: DATETIME, Not Null
- payment_status: VARCHAR(20), Not Null, Default 'Pending'

**Design Notes:**
- `patient_id` links each appointment to a patient.
- `doctor_id` links each appointment to a doctor.
- `status` can store values such as Scheduled, Completed, Cancelled.
- `appointment_start` and `appointment_end` are better than one single datetime because duration matters.
- A doctor should not be allowed to have overlapping appointments.
- This overlap rule is usually enforced in application logic or service layer, because MySQL alone does not easily prevent all overlapping time ranges with a simple constraint.
- For fixed one-hour slots, the system can also enforce one slot per doctor by checking availability before insert.

### Table: doctor_availability
- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key → doctors(id), Not Null
- available_date: DATE, Not Null
- start_time: TIME, Not Null
- end_time: TIME, Not Null
- is_available: BOOLEAN, Not Null, Default TRUE
- created_at: DATETIME, Not Null
- updated_at: DATETIME, Not Null

**Design Notes:**
- This table stores a doctor's available time slots separately from actual bookings.
- Patients should only be allowed to book time ranges marked as available.
- This design makes scheduling cleaner than storing everything inside the doctors table.

### Table: payments
- id: INT, Primary Key, Auto Increment
- appointment_id: INT, Foreign Key → appointments(id), Not Null, Unique
- amount: DECIMAL(10,2), Not Null
- payment_method: VARCHAR(30), Not Null
- payment_status: VARCHAR(20), Not Null
- paid_at: DATETIME, Null
- transaction_reference: VARCHAR(100), Null, Unique
- created_at: DATETIME, Not Null

**Design Notes:**
- Each appointment has at most one payment record in this design, so `appointment_id` is unique.
- `transaction_reference` helps track online payment records.
- Payment details are better kept separate from appointments for clarity.

### Relationship and Constraint Decisions
- A patient should **not** be hard deleted if they have past appointments.
- Instead of deleting a patient record, it is safer to set `is_active = FALSE` so appointment history is retained.
- A doctor should also not be hard deleted if there are past appointments tied to that doctor.
- Appointments should usually remain in the system for audit and medical history purposes.
- Email and phone validation should be handled in backend code later.
- Doctors should not be allowed to have overlapping appointments.
- This can be checked before inserting a new appointment by comparing the requested time range with existing scheduled appointments.

---

## MongoDB Collection Design

A flexible MongoDB collection can complement the MySQL schema well for storing prescription records because prescriptions may contain:
- free-form doctor notes
- varying numbers of medicines
- optional metadata
- nested refill instructions
- future schema changes without altering SQL tables

### Collection: prescriptions

```json
{
  "_id": "ObjectId('680000000000000000000001')",
  "appointmentId": 51,
  "patientId": 12,
  "doctorId": 7,
  "issuedAt": "2026-04-08T10:30:00Z",
  "diagnosis": "Seasonal flu",
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "frequency": "3 times daily",
      "durationDays": 5,
      "instructions": "Take after meals"
    },
    {
      "name": "Cough Syrup",
      "dosage": "10ml",
      "frequency": "2 times daily",
      "durationDays": 5,
      "instructions": "Shake well before use"
    }
  ],
  "doctorNotes": "Patient should rest and drink more water. Return if fever persists for more than 3 days.",
  "attachments": [
    {
      "fileName": "lab-result-apr-2026.pdf",
      "fileType": "application/pdf",
      "url": "/files/prescriptions/lab-result-apr-2026.pdf"
    }
  ],
  "tags": ["flu", "follow-up-needed"],
  "refillAllowed": false,
  "metadata": {
    "source": "doctor_portal",
    "version": 1
  }
}
