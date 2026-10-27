# 🎓 Student Attendance Tracker

A full-stack, responsive Student Attendance Tracking system designed for instructors and trainers to record student attendance, prevent duplicate registrations, calculate real-time attendance statistics (Present, Absent, Unmarked), and persist records reliably into a MySQL database with Spring Boot.

---

## 📌 Project Overview

- **Frontend**: React.js 18 with Vite, Lucide Icons, and custom responsive CSS.
- **Backend**: Java 21, Spring Boot 3.3.5 (REST API, Spring Data JPA, Hibernate, Jakarta Validation).
- **Database**: MySQL with unique constraints preventing duplicate roll numbers and duplicate attendance records for the same student and date.
- **Testing**: Automated JUnit 5 & Mockito test suite with in-memory H2 database.

---

## 🚀 Key Features

1. **Add Students**: Register students with their Name and Roll Number.
2. **Prevent Duplicate Roll Numbers**: Enforced both at the database layer (`UNIQUE` index) and application service layer with descriptive error feedback.
3. **Choose Attendance Date**: Interactive date picker with quick shortcuts ("Today", "Yesterday", and daily stepping).
4. **Display All Students for Date**: Displays every enrolled student alongside their status for the chosen date.
5. **Mark Present / Absent**: Intuitive toggles for individual students, plus batch shortcuts ("Mark All Present", "Mark All Absent", "Clear All").
6. **Save to MySQL**: Saves attendance through Spring Boot REST endpoints.
7. **Show Saved Records**: Loads existing saved attendance records automatically when selecting a date.
8. **Real-Time Totals**: Live calculation of **Total Students**, **Present**, **Absent**, **Unmarked**, and **Attendance Rate (%)**.
   > *Note*: Unmarked students are kept as pending and are **not** counted as absent.
9. **Seamless Date Switching**: Automatically pulls the selected date's history from the backend. Warns before switching if you have unsaved changes.
10. **Unique Constraint & Upsert**: Enforces a database-level unique constraint on `(student_id, attendance_date)`. Saving attendance for the same date updates the existing record rather than duplicating it.
11. **Student Directory**: View and manage enrolled students or remove students along with their history.

---

## 📁 Repository Structure

```text
student-attendance-tracker/
├── backend/
│   ├── pom.xml                               # Maven project definition (Spring Boot 3.3.5)
│   ├── mvnw / mvnw.cmd                       # Maven wrapper scripts
│   ├── .env.example                          # Backend environment template
│   └── src/
│       ├── main/
│       │   ├── java/com/attendance/tracker/
│       │   │   ├── AttendanceApplication.java
│       │   │   ├── config/CorsConfig.java
│       │   │   ├── controller/
│       │   │   │   ├── AttendanceController.java
│       │   │   │   └── StudentController.java
│       │   │   ├── dto/                      # Data Transfer Objects
│       │   │   ├── entity/                   # JPA Entities (Student, AttendanceRecord)
│       │   │   ├── exception/                # Global exception handling & error responses
│       │   │   ├── repository/               # Spring Data JPA repositories
│       │   │   └── service/                  # Business & Upsert logic
│       │   └── resources/
│       │       └── application.properties    # Production/Dev DB configuration
│       └── test/
│           ├── java/com/attendance/tracker/  # Comprehensive unit & MVC test suite
│           └── resources/application.properties # H2 test configuration
├── frontend/
│   ├── package.json                          # Vite React dependencies
│   ├── vite.config.js                        # Proxy configuration (/api -> localhost:8080)
│   ├── index.html
│   ├── .env.example                          # Frontend environment template
│   └── src/
│       ├── main.jsx
│       ├── App.jsx                           # Core state management & workflow
│       ├── index.css                         # Clean, modern, responsive styling
│       ├── components/
│       │   ├── Header.jsx
│       │   ├── StatsOverview.jsx
│       │   ├── DateSelector.jsx
│       │   ├── AttendanceTable.jsx
│       │   ├── AddStudentModal.jsx
│       │   ├── StudentDirectoryModal.jsx
│       │   └── AlertBanner.jsx
│       └── services/api.js                   # API client layer
├── .env.example                              # Root environment template
├── .gitignore                                # Excludes node_modules, target, logs, secrets
└── README.md                                 # Full documentation
```

---

## 🛠️ Prerequisites

Before running the application, make sure you have the following installed on your machine:

- **Java JDK**: Version 21 (or 17+)
- **Node.js**: Version 18+ and `npm`
- **MySQL Server**: Version 8.0+
- **Maven**: Version 3.8+ (optional, or use `./backend/mvnw`)
- **VS Code**: Recommended with Java Extension Pack & ES7 React extensions

---

## 🗄️ Database Setup (MySQL)

1. Start your local MySQL service:
   ```bash
   # On macOS (Homebrew)
   brew services start mysql

   # On Linux (systemd)
   sudo systemctl start mysql

   # On Windows
   net start MySQL80
   ```

2. Open the MySQL shell as `root` (or your MySQL user):
   ```bash
   mysql -u root -p
   ```

3. Create the database (Spring Boot will automatically create and update the required tables):
   ```sql
   CREATE DATABASE IF NOT EXISTS attendance_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

4. Verify database creation:
   ```sql
   SHOW DATABASES;
   EXIT;
   ```

---

## ⚙️ Configuration & Environment Variables

Copy `.env.example` or adjust credentials to match your local setup:

### Backend Configuration
File: `backend/src/main/resources/application.properties` (or set environment variables):
```properties
server.port=8080
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3306/attendance_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:root}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:}
spring.jpa.hibernate.ddl-auto=update
```

If your MySQL `root` user has a password (e.g. `secret123`), you can export it in your shell:
```bash
export SPRING_DATASOURCE_PASSWORD=secret123
```

---

## 🏃 Running the Application

You can open the project root in **VS Code**:
```bash
code "/Users/mattasathishgoud/Downloads/student attednance tracker"
```

### Step 1: Start the Backend (Spring Boot)

Open a terminal in the `backend/` directory:
```bash
cd backend

# Run with Maven Wrapper
./mvnw spring-boot:run

# Or run with system Maven:
mvn spring-boot:run
```
The REST API will start at **`http://localhost:8080`**.

### Step 2: Start the Frontend (React + Vite)

Open a second terminal in the `frontend/` directory:
```bash
cd frontend

# Install dependencies (if not already installed)
npm install

# Start Vite development server
npm run dev
```
Open your browser and navigate to **`http://localhost:5173`**.

---

## 🧪 Running Tests & Build Verification

### Backend Tests
The backend includes automated tests covering duplicate validation, upsert mechanics, daily count calculations, and controller endpoints using an in-memory H2 database:
```bash
cd backend
mvn clean test
```

To package the backend executable JAR:
```bash
cd backend
mvn clean package -DskipTests
# The packaged JAR will be generated at: backend/target/student-attendance-tracker-backend-1.0.0.jar
```

### Frontend Build
To verify the production build of the frontend:
```bash
cd frontend
npm run build
```

---

## 📡 REST API Documentation

### 1. Student Management (`/api/students`)

| Method | Endpoint | Description | Request Body | Response |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/students` | Get all enrolled students ordered by roll number | None | `200 OK` (Array of Students) |
| `POST` | `/api/students` | Register a new student | `{"name": "Alice", "rollNumber": "CS-101"}` | `201 Created` or `409 Conflict` |
| `GET` | `/api/students/{id}` | Get student by ID | None | `200 OK` or `404 Not Found` |
| `DELETE` | `/api/students/{id}` | Remove student & attendance | None | `204 No Content` |

### 2. Attendance Tracking (`/api/attendance`)

| Method | Endpoint | Description | Request Body / Query Params |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/attendance/overview` | Get attendance sheet and stats for a date | `?date=YYYY-MM-DD` |
| `GET` | `/api/attendance` | Get saved records for a date | `?date=YYYY-MM-DD` |
| `POST` | `/api/attendance/batch` | Batch save or update attendance for a date | `{"date": "YYYY-MM-DD", "records": [{"studentId": 1, "status": "PRESENT"}]}` |
| `POST` | `/api/attendance/single` | Upsert single student attendance | `?studentId=1&date=YYYY-MM-DD&status=PRESENT` |

---

## 🔧 Database Schema Constraints

### `students` Table
- `id`: `BIGINT AUTO_INCREMENT PRIMARY KEY`
- `name`: `VARCHAR(100) NOT NULL`
- `roll_number`: `VARCHAR(50) NOT NULL UNIQUE` (Constraint: `uk_student_roll_number`)
- `created_at`: `DATETIME NOT NULL`

### `attendance_records` Table
- `id`: `BIGINT AUTO_INCREMENT PRIMARY KEY`
- `student_id`: `BIGINT NOT NULL REFERENCES students(id)`
- `attendance_date`: `DATE NOT NULL`
- `status`: `VARCHAR(20) NOT NULL` (`PRESENT` or `ABSENT`)
- `created_at`: `DATETIME NOT NULL`
- `updated_at`: `DATETIME NOT NULL`
- **Unique Constraint**: `(student_id, attendance_date)` via `uk_student_attendance_date`. This guarantees at the database level that no student can have multiple attendance records on the same date.

---

## 🔍 Troubleshooting

1. **MySQL Connection Refused (`Can't connect to local MySQL server`)**:
   - Check if MySQL is running: `brew services list` or `mysqladmin ping -u root`.
   - Start MySQL: `brew services start mysql`.
2. **Access Denied for user 'root'@'localhost'**:
   - Update your password in `backend/src/main/resources/application.properties` or set `SPRING_DATASOURCE_PASSWORD=your_password`.
3. **Port 8080 or 5173 Already in Use**:
   - Change `server.port=8081` in `application.properties` or start Vite with `npm run dev -- --port 5174`.
4. **CORS Issues**:
   - The backend includes `CorsConfig.java` configured to accept requests from all localhost origins, and Vite is configured with a development proxy `/api` pointing to `http://localhost:8080`.

---

## 👤 Author
- GitHub: [@nandinimatta90-dotcom](https://github.com/nandinimatta90-dotcom)
