import { API_BASE_URL } from '../config/config.js';

const DOCTOR_API = `${API_BASE_URL}/doctor`;

export async function getDoctors() {
    try {
        const response = await fetch(`${DOCTOR_API}`);
        const data = await response.json();
        return data.doctors || [];
    } catch (error) {
        console.error("Error fetching doctors:", error);
        return [];
    }
}

export async function deleteDoctor(id, token) {
    try {
        const response = await fetch(`${DOCTOR_API}/${id}/${token}`, {
            method: 'DELETE'
        });
        const data = await response.json();
        return {
            success: response.ok,
            message: data.message || (response.ok ? "Doctor deleted." : "Deletion failed.")
        };
    } catch (error) {
        console.error("Error deleting doctor:", error);
        return { success: false, message: "An error occurred." };
    }
}

export async function saveDoctor(doctor, token) {
    try {
        const response = await fetch(`${DOCTOR_API}/${token}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(doctor)
        });
        const data = await response.json();
        return {
            success: response.ok,
            message: data.message || (response.ok ? "Doctor saved." : "Save failed.")
        };
    } catch (error) {
        console.error("Error saving doctor:", error);
        return { success: false, message: "An error occurred." };
    }
}

export async function filterDoctors(name, time, specialty) {
    try {
        const params = new URLSearchParams();
        if (name) params.append('name', name);
        if (time) params.append('time', time);
        if (specialty) params.append('specialty', specialty);

        const response = await fetch(`${DOCTOR_API}?${params.toString()}`);
        if (response.ok) {
            const data = await response.json();
            return data;
        } else {
            console.error("Filter failed:", response.status);
            return { doctors: [] };
        }
    } catch (error) {
        alert("Error filtering doctors. Please try again.");
        console.error(error);
        return { doctors: [] };
    }
}
