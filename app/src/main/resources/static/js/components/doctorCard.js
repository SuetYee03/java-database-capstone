import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";
import { showBookingOverlay } from "../loggedPatient.js";

export function createDoctorCard(doctor) {
    const card = document.createElement("div");
    card.classList.add("doctor-card");

    const role = localStorage.getItem("userRole");

    const infoDiv = document.createElement("div");
    infoDiv.classList.add("doctor-info");

    const name = document.createElement("h3");
    name.textContent = doctor.name;

    const specialization = document.createElement("p");
    specialization.textContent = `Specialization: ${doctor.specialization}`;

    const email = document.createElement("p");
    email.textContent = `Email: ${doctor.email}`;

    const availability = document.createElement("p");
    availability.textContent = `Availability: ${Array.isArray(doctor.availability) ? doctor.availability.join(", ") : doctor.availability}`;

    infoDiv.appendChild(name);
    infoDiv.appendChild(specialization);
    infoDiv.appendChild(email);
    infoDiv.appendChild(availability);

    const actionsDiv = document.createElement("div");
    actionsDiv.classList.add("card-actions");

    if (role === "admin") {
        const removeBtn = document.createElement("button");
        removeBtn.textContent = "Delete";

        removeBtn.addEventListener("click", async () => {
            const confirmDelete = confirm(`Are you sure you want to delete Dr. ${doctor.name}?`);
            if (!confirmDelete) return;

            const token = localStorage.getItem("token");
            if (!token) {
                alert("Session expired. Please log in again.");
                window.location.href = "/";
                return;
            }

            try {
                const result = await deleteDoctor(doctor.id, token);

                if (result && result.success) {
                    alert("Doctor deleted successfully.");
                    card.remove();
                } else {
                    alert(result?.message || "Failed to delete doctor.");
                }
            } catch (error) {
                console.error("Delete doctor error:", error);
                alert("An error occurred while deleting the doctor.");
            }
        });

        actionsDiv.appendChild(removeBtn);
    } else if (role === "patient") {
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";

        bookNow.addEventListener("click", () => {
            alert("Patient needs to login first.");
        });

        actionsDiv.appendChild(bookNow);
    } else if (role === "loggedPatient") {
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";

        bookNow.addEventListener("click", async (e) => {
            const token = localStorage.getItem("token");

            if (!token) {
                alert("Session expired. Please log in again.");
                localStorage.removeItem("token");
                localStorage.setItem("userRole", "patient");
                window.location.href = "/pages/patientDashboard.html";
                return;
            }

            try {
                const patientData = await getPatientData(token);
                showBookingOverlay(e, doctor, patientData);
            } catch (error) {
                console.error("Booking error:", error);
                alert("Unable to load patient data for booking.");
            }
        });

        actionsDiv.appendChild(bookNow);
    }

    card.appendChild(infoDiv);
    card.appendChild(actionsDiv);

    return card;
}