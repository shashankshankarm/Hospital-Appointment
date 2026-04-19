# Hospital Appointment API

Spring Boot backend for hospital appointment and patient records management.

## Run the project

### 1) Prerequisites
- Java 17
- Maven 3.9+

### 2) Go to project directory
```bash
cd HospitalAppointment/HospitalAppointment
```

### 3) Set required environment variables
`JWT_SECRET` is mandatory and must be at least 32 characters.

```bash
export JWT_SECRET="this-is-a-very-secure-secret-key-12345"
export BOOTSTRAP_ADMIN_PASSWORD="Admin@123"
```

Optional:
- `BOOTSTRAP_ADMIN_USERNAME` (default: `admin`)
- `BOOTSTRAP_ADMIN_EMAIL` (default: `admin@hospital.local`)
- `SERVER_PORT` (default: `8080`)

### 4) Start application
```bash
mvn spring-boot:run
```

### 5) Base URL
```text
http://localhost:8080
```

Swagger UI:
```text
http://localhost:8080/swagger-ui.html
```

## Authentication for Postman

1. Login with bootstrap admin user:
   - username: `admin` (or `BOOTSTRAP_ADMIN_USERNAME`)
   - password: value of `BOOTSTRAP_ADMIN_PASSWORD`
2. Copy `accessToken` from login response.
3. In Postman, set header:
   - `Authorization: Bearer <accessToken>`

---

## API endpoints (Postman)

### Auth

#### Register patient
- **Method:** `POST`
- **URL:** `http://localhost:8080/api/auth/register/patient`
- **Auth:** No
- **Expected status:** `201 Created`
- **Body**
```json
{
  "username": "patient1",
  "email": "patient1@example.com",
  "password": "Patient@123",
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "1995-06-15",
  "gender": "Male",
  "phone": "9876543210",
  "address": "Chennai"
}
```
- **Expected response (shape)**
```json
{
  "accessToken": "<jwt>",
  "refreshToken": "<refresh-token>",
  "tokenType": "Bearer",
  "expiresInSeconds": 900,
  "username": "patient1",
  "roles": ["PATIENT"]
}
```

#### Login
- **Method:** `POST`
- **URL:** `http://localhost:8080/api/auth/login`
- **Auth:** No
- **Expected status:** `200 OK`
- **Body**
```json
{
  "username": "admin",
  "password": "Admin@123"
}
```

#### Refresh token
- **Method:** `POST`
- **URL:** `http://localhost:8080/api/auth/refresh`
- **Auth:** No
- **Expected status:** `200 OK`
- **Body**
```json
{
  "refreshToken": "<refresh-token>"
}
```

### Patients

#### Create patient
- **Method:** `POST`
- **URL:** `http://localhost:8080/api/patients`
- **Auth:** Bearer token (`ADMIN` or `RECEPTIONIST`)
- **Expected status:** `201 Created`
- **Body**
```json
{
  "firstName": "Priya",
  "lastName": "Sharma",
  "dateOfBirth": "1998-04-21",
  "gender": "Female",
  "phone": "9000012345",
  "email": "priya@example.com",
  "address": "Bangalore"
}
```

#### Get patient by ID
- **Method:** `GET`
- **URL:** `http://localhost:8080/api/patients/{id}`
- **Auth:** Bearer token (`ADMIN`, `RECEPTIONIST`, `DOCTOR`)
- **Expected status:** `200 OK`

#### Search patients
- **Method:** `GET`
- **URL:** `http://localhost:8080/api/patients?query=priya`
- **Auth:** Bearer token (`ADMIN`, `RECEPTIONIST`, `DOCTOR`)
- **Expected status:** `200 OK`

#### Update patient
- **Method:** `PUT`
- **URL:** `http://localhost:8080/api/patients/{id}`
- **Auth:** Bearer token (`ADMIN` or `RECEPTIONIST`)
- **Expected status:** `200 OK`
- **Body:** same as create patient body

### Doctors

#### Create doctor
- **Method:** `POST`
- **URL:** `http://localhost:8080/api/doctors`
- **Auth:** Bearer token (`ADMIN`)
- **Expected status:** `201 Created`
- **Body**
```json
{
  "firstName": "Anita",
  "lastName": "Rao",
  "specialty": "Cardiology",
  "available": true
}
```

#### Get doctor by ID
- **Method:** `GET`
- **URL:** `http://localhost:8080/api/doctors/{id}`
- **Auth:** Bearer token (`ADMIN`, `RECEPTIONIST`, `DOCTOR`)
- **Expected status:** `200 OK`

#### List doctors
- **Method:** `GET`
- **URL:** `http://localhost:8080/api/doctors`
- **Auth:** Bearer token (`ADMIN`, `RECEPTIONIST`, `DOCTOR`, `PATIENT`)
- **Expected status:** `200 OK`

With specialty filter:
`http://localhost:8080/api/doctors?specialty=Cardio`

#### Update doctor
- **Method:** `PUT`
- **URL:** `http://localhost:8080/api/doctors/{id}`
- **Auth:** Bearer token (`ADMIN`)
- **Expected status:** `200 OK`
- **Body:** same as create doctor body

### Appointments

#### Create appointment
- **Method:** `POST`
- **URL:** `http://localhost:8080/api/appointments`
- **Auth:** Bearer token (`ADMIN`, `RECEPTIONIST`, `PATIENT`)
- **Expected status:** `201 Created`
- **Body**
```json
{
  "patientId": 1,
  "doctorId": 1,
  "startTime": "2030-01-01T10:00:00",
  "endTime": "2030-01-01T10:30:00",
  "reason": "General checkup"
}
```

#### Reschedule appointment
- **Method:** `PATCH`
- **URL:** `http://localhost:8080/api/appointments/{id}/reschedule`
- **Auth:** Bearer token (`ADMIN`, `RECEPTIONIST`, `PATIENT`)
- **Expected status:** `200 OK`
- **Body**
```json
{
  "startTime": "2030-01-01T11:00:00",
  "endTime": "2030-01-01T11:30:00"
}
```

#### Cancel appointment
- **Method:** `PATCH`
- **URL:** `http://localhost:8080/api/appointments/{id}/cancel`
- **Auth:** Bearer token (`ADMIN`, `RECEPTIONIST`, `PATIENT`)
- **Expected status:** `200 OK`

#### Doctor schedule by date
- **Method:** `GET`
- **URL:** `http://localhost:8080/api/appointments/doctor/{doctorId}?date=2030-01-01`
- **Auth:** Bearer token (`ADMIN`, `RECEPTIONIST`, `DOCTOR`)
- **Expected status:** `200 OK`

---

## Expected error response format

If validation/auth/business errors happen, response format is:
```json
{
  "timestamp": "2026-04-19T16:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": [
    "fieldName: error message"
  ]
}
```

Common status codes:
- `400` validation/business rule failure
- `401` invalid/expired token
- `403` role not allowed
- `404` entity not found
