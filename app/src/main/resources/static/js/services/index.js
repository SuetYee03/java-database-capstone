import { openModal } from '../components/modals.js';
import { API_BASE_URL } from '../config/config.js';

const ADMIN_API = `${API_BASE_URL}/admin`;
const DOCTOR_API = `${API_BASE_URL}/doctor`;
const PATIENT_API = `${API_BASE_URL}/patient`;

window.onload = function() {
    const adminBtn = document.getElementById("adminBtn");
    const doctorBtn = document.getElementById("doctorBtn");
    const patientBtn = document.getElementById("patientBtn");

    if (adminBtn) {
        adminBtn.addEventListener("click", () => {
             // If already logged in, selectRole will redirect. Otherwise open modal.
             const token = localStorage.getItem('token');
             if (token && localStorage.getItem('userRole') === 'admin') {
                 selectRole('admin');
             } else {
                 openModal('adminLogin');
             }
        });
    }

    if (doctorBtn) {
        doctorBtn.addEventListener("click", () => {
             const token = localStorage.getItem('token');
             if (token && localStorage.getItem('userRole') === 'doctor') {
                 selectRole('doctor');
             } else {
                 openModal('doctorLogin');
             }
        });
    }

    if (patientBtn) {
        patientBtn.addEventListener("click", () => {
             const token = localStorage.getItem('token');
             if (token && localStorage.getItem('userRole') === 'patient') {
                 selectRole('patient');
             } else {
                 openModal('patientLogin');
             }
        });
    }
}

window.adminLoginHandler = async function() {
    const usernameInput = document.getElementById("username");
    const passwordInput = document.getElementById("password");

    const admin = {
        username: usernameInput.value,
        password: passwordInput.value
    };

    try {
        const response = await fetch(`${ADMIN_API}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(admin)
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem("token", data.token);
            selectRole('admin');
        } else {
            alert("Invalid credentials / Login failed");
        }
    } catch (error) {
        alert("Server error occurred");
        console.error(error);
    }
}

window.doctorLoginHandler = async function() {
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");

    const doctor = {
        email: emailInput.value,
        password: passwordInput.value
    };

    try {
        const response = await fetch(`${DOCTOR_API}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(doctor)
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem("token", data.token);
            selectRole('doctor');
        } else {
            alert("Invalid credentials / Login failed");
        }
    } catch (error) {
        alert("Server error occurred");
        console.error(error);
    }
}

window.loginPatient = async function() {
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");

    const patient = {
        email: emailInput.value,
        password: passwordInput.value
    };

    try {
        const response = await fetch(`${PATIENT_API}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(patient)
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem("token", data.token);
            selectRole('patient');
        } else {
            alert("Invalid credentials / Login failed");
        }
    } catch (error) {
        alert("Server error occurred");
        console.error(error);
    }
}
