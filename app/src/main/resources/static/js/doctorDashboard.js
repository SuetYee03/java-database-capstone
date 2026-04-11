import { getAllAppointments, getDoctorAllAppointments } from './services/appointmentRecordService.js';
import { createPatientRow } from './components/patientRows.js';

const tableBody = document.getElementById("patientTableBody");
const searchBar = document.getElementById("searchBar");
const todayBtn = document.getElementById("todayAppointmentsBtn");
const allBtn = document.getElementById("allAppointmentsBtn");
const datePicker = document.getElementById("appointmentDate");
const token = localStorage.getItem("token");

let selectedDate = new Date().toISOString().split('T')[0];
let patientName = "null";
let isViewAll = false;

// Set initial date in picker
if (datePicker) datePicker.value = selectedDate;

document.addEventListener("DOMContentLoaded", () => {
    loadAppointments();

    if (searchBar) {
        searchBar.addEventListener('input', (e) => {
            const val = e.target.value.trim();
            patientName = val === "" ? "null" : val;
            loadAppointments();
        });
    }

    if (todayBtn) {
        todayBtn.addEventListener('click', () => {
            isViewAll = false;
            selectedDate = new Date().toISOString().split('T')[0];
            if (datePicker) datePicker.value = selectedDate;
            loadAppointments();
        });
    }

    if (allBtn) {
        allBtn.addEventListener('click', () => {
            isViewAll = true;
            if (datePicker) datePicker.value = "";
            loadAppointments();
        });
    }

    if (datePicker) {
        datePicker.addEventListener('change', (e) => {
            isViewAll = false;
            selectedDate = e.target.value;
            loadAppointments();
        });
    }
});

async function loadAppointments() {
    try {
        if (!token) {
            console.error("No token found, redirecting to login.");
            return;
        }

        let response;
        if (isViewAll) {
            response = await getDoctorAllAppointments(patientName, token);
        } else {
            response = await getAllAppointments(selectedDate, patientName, token);
        }
        const appointments = response?.appointments || [];
        tableBody.innerHTML = "";

        if (appointments.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="5" style="text-align:center;">
                        No Appointments found for this date. <br>
                        <span style="color: #666; font-size: 0.9em;">(Tip: Try selecting 2025 in the calendar if looking for course data)</span>
                    </td>
                </tr>`;
            return;
        }

        appointments.forEach(appointment => {
            const patient = {
                id: appointment.patientId,
                name: appointment.patientName,
                phone: appointment.patientPhone,
                email: appointment.patientEmail,
                date: appointment.appointmentDate,
                time: appointment.appointmentTimeOnly
            };
            // Note: appointment object from backend likely has the properties needed
            const row = createPatientRow(patient, appointment.id, appointment.doctorId);
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error("Error loading appointments:", error);
        tableBody.innerHTML = '<tr><td colspan="5" style="text-align:center;">Error loading appointments. Try again later.</td></tr>';
    }
}
