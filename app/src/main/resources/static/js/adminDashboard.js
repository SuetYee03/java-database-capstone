import { openModal } from './components/modals.js';
import { getDoctors, filterDoctors, saveDoctor } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';

// Attach Add Doctor button listener
const addDoctorBtn = document.getElementById('addDoctorBtn');
if (addDoctorBtn) {
    addDoctorBtn.addEventListener('click', () => openModal('addDoctor'));
}

// Load all doctors on page load
document.addEventListener('DOMContentLoaded', () => {
    loadDoctorCards();

    const searchBar = document.getElementById('searchBar');
    const timeFilter = document.getElementById('timeFilter');
    const specialtyFilter = document.getElementById('specialtyFilter');

    if (searchBar) searchBar.addEventListener('input', filterDoctorsOnChange);
    if (timeFilter) timeFilter.addEventListener('change', filterDoctorsOnChange);
    if (specialtyFilter) specialtyFilter.addEventListener('change', filterDoctorsOnChange);
});

async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        const content = document.getElementById('content');
        content.innerHTML = '';
        if (!doctors || doctors.length === 0) {
            content.innerHTML = '<p>No doctors found.</p>';
            return;
        }
        doctors.forEach(doctor => {
            const card = createDoctorCard(doctor);
            content.appendChild(card);
        });
    } catch (error) {
        console.error("Error loading doctor cards:", error);
    }
}

async function filterDoctorsOnChange() {
    const name = document.getElementById('searchBar')?.value?.trim() || null;
    const time = document.getElementById('timeFilter')?.value || null;
    const specialty = document.getElementById('specialtyFilter')?.value || null;

    try {
        const data = await filterDoctors(
            name || null,
            time || null,
            specialty || null
        );
        const doctors = data.doctors || [];
        const content = document.getElementById('content');
        content.innerHTML = '';
        if (doctors.length === 0) {
            content.innerHTML = '<p>No doctors found with the given filters.</p>';
        } else {
            doctors.forEach(doc => content.appendChild(createDoctorCard(doc)));
        }
    } catch (error) {
        alert("Error filtering doctors.");
        console.error(error);
    }
}

function renderDoctorCards(doctors) {
    const content = document.getElementById('content');
    content.innerHTML = '';
    doctors.forEach(doc => content.appendChild(createDoctorCard(doc)));
}

window.adminAddDoctor = async function () {
    const name = document.getElementById('doctorName')?.value?.trim();
    const email = document.getElementById('doctorEmail')?.value?.trim();
    const phone = document.getElementById('doctorPhone')?.value?.trim();
    const password = document.getElementById('doctorPassword')?.value?.trim();
    const specialization = document.getElementById('specialization')?.value;
    const availabilityCheckboxes = document.querySelectorAll('input[name="availability"]:checked');
    const availability = Array.from(availabilityCheckboxes).map(cb => cb.value);

    const token = localStorage.getItem('token');
    if (!token) {
        alert("Session expired. Please log in again.");
        return;
    }

    const doctor = { name, email, phone, password, specialty: specialization, availableTimes: availability };

    const result = await saveDoctor(doctor, token);
    if (result.success) {
        alert("Doctor added successfully!");
        document.getElementById('modal').style.display = 'none';
        location.reload();
    } else {
        alert(result.message || "Failed to add doctor.");
    }
}
