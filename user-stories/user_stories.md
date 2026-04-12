# Admin Add Doctor Story Template

**Title:**
As an admin, I want to add doctors to the portal, so that I can manage doctor records in the system.

**Acceptance Criteria:**
1. The admin can enter doctor details such as name, specialization, contact number, and email.
2. The system validates required fields before saving the record.
3. The system stores the doctor profile successfully when valid information is submitted.
4. The system shows an error message when required fields are missing or invalid.

**Priority:** High
**Story Points:** 5

**Notes:**
1. Required fields should be clearly marked.
2. Duplicate doctor records should be prevented if needed.


# Admin Delete Doctor Story Template

**Title:**
As an admin, I want to delete a doctor's profile from the portal, so that I can remove outdated or invalid doctor records.

**Acceptance Criteria:**
1. The admin can select a doctor profile to delete.
2. The system asks for confirmation before deleting the doctor profile
3. The system removes the doctor profile from the portal after confirmation


**Priority:** High
**Story Points:** 5

**Notes:**
1. Related appointment records should be handled carefully
2. Soft delete may be safer than permanent delete


# Admin Login Story Template

**Title:**
As a admin, I want login into the portal, so that I can manage the platform securely._

**Acceptance Criteria:**

1. The admin can log in using a valid username and password.
2. The system grants access to the admin dashboard after successful login.
3. The system displays an error message when the username or password is invalid.
4. The password field masks the entered characters.
5. The system prevents access to admin features unless the admin is logged in.

**Priority:** High 
**Story Points:** 7 
**Notes:**

**Notes:**
1. Login is for admin users only.
2. Validation should be done securely.
3.Error messages should not reveal whether the username or password is wrong.

# Admin Log Out Story Template

**Title:**
As a admin, I want log out , so that the system access be protected.

**Acceptance Criteria:**

1. The admin can click a logout button or link to end the session.
2. The system redirects the admin to the login page after logout.
3. The admin cannot access protected pages after logging out unless they log in again.


**Priority:** Low
**Story Points:** 3 
**Notes:**

**Notes:**
1. Session should be cleared after logout.
2. This is a security-related function.


# Admin Store  Story Template

**Title:**
As a system administrator, I want to run a stored procedure in MySQL CLI, so that I can get the number of appointments per month and track usage statistics.

**Acceptance Criteria:**
1. The system administrator can execute the stored procedure successfully in MySQL CLI
2. The stored procedure returns the number of appointments for each month
3. The output is shown in a readable format for usage tracking


**Priority:** Medium
**Story Points:** 3

**Notes:**
1. This approach has issues because it is more of a technical task than a portal user story
2. A portal-based reporting feature would usually be stronger for an assignment


# Doctor login Story Template

**Title:**
As a doctor, I want to log into the portal, so that I can manage my appointments.

**Acceptance Criteria:**
1. The doctor can log in using a registered email and password
2. The system grants access when the login credentials are correct
3. The system shows an error message when the login credentials are invalid


**Priority:** High
**Story Points:** 3

**Notes:**
1. Login is required before accessing doctor features

# Doctor logout Story Template

**Title:**
As a doctor, I want to log out of the portal, so that I can protect my data.

**Acceptance Criteria:**
1. The doctor can click the logout button to end the current session
2. The system redirects the doctor to the login page or home page after logout
3. The doctor cannot access protected pages unless logged in again

**Priority:** High
**Story Points:** 2

**Notes:**
1. Session data should be cleared after logout

# Doctor mark availiability time slot Story Template

**Title:**
As a doctor, I want to mark my unavailability, so that patients can only see my available slots.

**Acceptance Criteria:**
1. The doctor can select dates and time slots to mark as unavailable
2. The system updates the schedule after the unavailability is saved
3. Patients cannot book appointments during unavailable time slots

**Priority:** High
**Story Points:** 5

**Notes:**
1. Existing booked appointments should not be overwritten
2. The system should prevent conflicting schedule updates

# Doctor update Story Template

**Title:**
As a doctor, I want to update my profile with specialization and contact information, so that patients have up-to-date information.

**Acceptance Criteria:**
1. The doctor can edit profile details such as specialization and contact information
2. The system validates required fields before saving changes
3. The updated profile information is displayed correctly after saving

**Priority:** Medium
**Story Points:** 3

**Notes:**
1. Only the doctor should be allowed to update their own profile

# Doctor view Appointment Story Template

**Title:**
As a doctor, I want to view my appointment calendar, so that I can stay organized.

**Acceptance Criteria:**
1. The doctor can view a calendar of scheduled appointments after logging in
2. The system displays appointment details such as patient name, date, and time
3. The calendar shows appointments in the correct date and time order

**Priority:** High
**Story Points:** 5

**Notes:**
1. Calendar view should be easy to read and update

# Doctor view upcoming appointment Story Template

**Title:**
As a doctor, I want to view the patient details for upcoming appointments, so that I can be prepared.

**Acceptance Criteria:**
1. The doctor can view patient details for upcoming appointments after logging in
2. The system displays relevant details such as patient name, appointment date, and time
3. Only upcoming appointment details are shown in this section

**Priority:** High
**Story Points:** 5

**Notes:**
1. Patient information should be protected and only accessible to authorized doctors

# Patient Login to book appointment Story Template

**Title:**
As a patient, I want to log in and book an hour-long appointment with a doctor, so that I can consult with the doctor.

**Acceptance Criteria:**
1. The patient must log in before booking an appointment
2. The patient can select a doctor, date, and available one-hour time slot
3. The system saves the booking successfully and shows a confirmation message


**Priority:** High
**Story Points:** 5

**Notes:**
1. Only available time slots should be shown
2. Double booking should be prevented

# Patient Login Story Template

**Title:**
As a patient, I want to log into the portal, so that I can manage my bookings.

**Acceptance Criteria:**
1. The patient can log in using a registered email and password
2. The system grants access when the login credentials are correct
3. The system shows an error message when the login credentials are invalid


**Priority:** High
**Story Points:** 3

**Notes:**
1. Login is required before accessing booking management features

# Patient Log out Story Template

**Title:**
As a patient, I want to log out of the portal, so that I can secure my account.

**Acceptance Criteria:**
1. The patient can click the logout button to end the current session
2. The system redirects the patient to the login page or home page after logout
3. The patient cannot access protected pages unless logged in again


**Priority:** High
**Story Points:** 2

**Notes:**
1. Session data should be cleared after logout

# Patient Sign in Story Template

**Title:**
As a patient, I want to sign up using my email and password, so that I can book appointments.

**Acceptance Criteria:**
1. The patient can enter an email and password to create an account
2. The system validates the email format and required fields
3. The system creates the patient account successfully when valid information is submitted


**Priority:** High
**Story Points:** 3

**Notes:**
1. Email should be unique for each patient account

# Patient view all the book appointment Story Template

**Title:**
As a patient, I want to view my upcoming appointments, so that I can prepare accordingly.

**Acceptance Criteria:**
1. The patient can view a list of upcoming appointments after logging in
2. The system displays appointment details such as doctor name, date, and time
3. Past appointments are not mixed with upcoming appointments


**Priority:** Medium 
**Story Points:** 3

**Notes:**
1. Upcoming appointments should be clearly organized for easy viewing

# Patient view Story Template

**Title:**
As a patient, I want to view a list of doctors without logging in, so that I can explore available options before registering.

**Acceptance Criteria:**
1. The patient can access the doctor list without creating an account or logging in
2. The system displays doctor details such as name, specialization, and availability
3. The patient cannot access booking features without logging in


**Priority:** High
**Story Points:** 3

**Notes:**
1. This supports guest users who want to browse before registration
