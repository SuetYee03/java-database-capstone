function renderHeader() {
    const headerDiv = document.getElementById("header");
    if (!headerDiv) return;

    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    if ((role === "loggedpatient" || role === "admin" || role === "doctor") && !token) {
        localStorage.removeItem("userRole");
        alert("Session expired or invalid login. Please log in again.");
        window.location.href = "/";
        return;
    }

    const pathPrefix = window.location.pathname.endsWith(".html") || window.location.pathname.includes("/pages/") ? ".." : ".";

    let headerContent = `
        <header class="header">
            <div class="logo-section">
                <img src="${pathPrefix}/assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
                <span class="logo-title">Hospital CMS</span>
            </div>
            <nav class="nav-links">
                <a href="${pathPrefix}/" class="nav-link">Main Home</a>
    `;

    if (role === "admin") {
        headerContent += `
            <button id="addDocBtn" class="adminBtn">Add Doctor</button>
            <a href="#" id="logoutBtn">Logout</a>
        `;
    } else if (role === "doctor") {
        headerContent += `
            <button id="doctorHomeBtn" class="adminBtn">Home</button>
            <a href="#" id="logoutBtn">Logout</a>
        `;
    } else if (role === "patient") {
        headerContent += `
            <button id="patientLogin" class="adminBtn">Login</button>
            <button id="patientSignup" class="adminBtn">Sign Up</button>
        `;
    } else if (role === "loggedpatient") {
        headerContent += `
            <button id="homeBtn" class="adminBtn">Dashboard</button>
            <button id="patientAppointments" class="adminBtn">Appointments</button>
            <a href="#" id="logoutPatientBtn">Logout</a>
        `;
    }

    headerContent += `
            </nav>
        </header>
    `;

    headerDiv.innerHTML = headerContent;
    attachHeaderButtonListeners();
}

function attachHeaderButtonListeners() {
    const addDocBtn = document.getElementById("addDocBtn");
    if (addDocBtn) {
        addDocBtn.addEventListener("click", function () {
            openModal("addDoctor");
        });
    }

    const doctorHomeBtn = document.getElementById("doctorHomeBtn");
    if (doctorHomeBtn) {
        doctorHomeBtn.addEventListener("click", function () {
            window.location.href = "/pages/doctorDashboard.html";
        });
    }

    const patientLogin = document.getElementById("patientLogin");
    if (patientLogin) {
        patientLogin.addEventListener("click", function () {
            openModal("patientLogin");
        });
    }

    const patientSignup = document.getElementById("patientSignup");
    if (patientSignup) {
        patientSignup.addEventListener("click", function () {
            openModal("patientSignup");
        });
    }

    const homeBtn = document.getElementById("homeBtn");
    if (homeBtn) {
        homeBtn.addEventListener("click", function () {
            window.location.href = "/pages/loggedPatientDashboard.html";
        });
    }

    const patientAppointments = document.getElementById("patientAppointments");
    if (patientAppointments) {
        patientAppointments.addEventListener("click", function () {
            window.location.href = "/pages/patientAppointments.html";
        });
    }

    const logoutBtn = document.getElementById("logoutBtn");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", function (event) {
            event.preventDefault();
            logout();
        });
    }

    const logoutPatientBtn = document.getElementById("logoutPatientBtn");
    if (logoutPatientBtn) {
        logoutPatientBtn.addEventListener("click", function (event) {
            event.preventDefault();
            logoutPatient();
        });
    }
}

function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    window.location.href = "/";
}

function logoutPatient() {
    localStorage.removeItem("token");
    localStorage.setItem("userRole", "patient");
    window.location.href = "/pages/patientDashboard.html";
}

renderHeader();