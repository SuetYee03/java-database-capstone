# Schema Architecture

## Section 1: Architecture Summary

This Smart Clinic Management System is built as a Spring Boot application that combines both MVC and REST architecture. MVC controllers are used for the Admin and Doctor dashboards, where Thymeleaf templates generate the web pages shown to users. REST controllers are used for other modules that exchange data in JSON format, making the system more flexible for frontend interaction and API-based operations.

The application is organized in layers. Client requests are first handled by the controllers, then passed to the service layer where the main business logic is processed. After that, the service layer communicates with the repository layer to access the databases. The system uses MySQL to store structured clinic data such as admins, doctors, patients, and appointments, while MongoDB is used to store prescription-related data. MySQL works with JPA entities, whereas MongoDB works with document models.

## Section 2: Numbered Flow of Data and Control

1. A user accesses a system feature such as the Admin Dashboard, Doctor Dashboard, or appointment-related page.
2. The request is sent to either an MVC controller or a REST controller depending on the module being used.
3. The controller receives the request and forwards it to the service layer for processing.
4. The service layer applies the business logic, such as validating login details, managing doctor records, or handling appointment operations.
5. The service layer calls the appropriate repository to retrieve, save, update, or delete data.
6. The repository interacts with either MySQL for relational clinic data or MongoDB for prescription data.
7. The processed result is returned back through the service and controller, then displayed as a Thymeleaf page or returned as a REST API response.
