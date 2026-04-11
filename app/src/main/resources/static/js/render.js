// render.js

function selectRole(role) {
  role = role.toLowerCase();
  setRole(role);
  const token = localStorage.getItem('token');
  if (role === "admin") {
    if (token) {
      window.location.href = `/pages/adminDashboard.html`;
    }
  } else if (role === "patient") {
    if (token) {
      window.location.href = "/pages/loggedPatientDashboard.html";
    }
  } else if (role === "doctor") {
    if (token) {
      window.location.href = `/pages/doctorDashboard.html`;
    }
  }
}


function renderContent() {
  const role = getRole();
  if (!role) {
    window.location.href = "/"; // if no role, send to role selection page
    return;
  }
}
