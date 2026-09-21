<div align="center">

<h1 align="center">
  <img src="https://readme-typing-svg.demolab.com?font=Fira+Code&weight=700&size=32&pause=1000&color=28A745&center=true&vCenter=true&width=550&lines=%F0%9F%93%95+Shift+Planner" alt="Shift Planner" />
</h1>

<p align="center">
  A full-stack web application for workforce schedule management with dynamic shift filtering and real-time conflict detection.
</p>

<!-- TECH BADGES -->
![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![Vite](https://img.shields.io/badge/Vite-646CFF?style=for-the-badge&logo=vite&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

<br />

<!-- QUICK NAVIGATION LINKS -->
<p align="center">
  <a href="#about-the-project">About</a> •
  <a href="#key-features">Features</a> •
  <a href="#application-demo">Key Visuals</a> •
  <a href="#database-architecture">Database</a> •
  <a href="#api-endpoints">API</a> •
  <a href="#local-setup">Installation</a>
</p>

</div>

---

## ![](https://img.shields.io/badge/💡_About_the_Project-007ACC?style=for-the-badge)

This is a **full-stack web application** designed and built as a capstone project for LaunchCode. **Shift Planner** addresses workforce scheduling challenges by providing employees and managers with a clean interface to view open shifts, claim work hours, and manage schedules in real time.

The application features a responsive React frontend paired with a Java Spring Boot backend and MySQL database. Built-in business logic prevents scheduling overlaps and detects shift conflicts dynamically before saving data.

---

## ![](https://img.shields.io/badge/✨_Key_Features-6DB33F?style=for-the-badge)

* 🔍 **Smart Filtering:** Instant filtering by time categories (*Morning*, *Afternoon*, *Evening*) and specific days of the week.
* ⚠️ **Conflict Detection:** Validation logic ensuring zero overlapping work schedules when claiming shifts.
* ⚡ **Responsive UI State:** Dynamic rendering updates across React components without full page reloads.
* 🛠️ **RESTful API Integration:** Seamless endpoints bridging the React frontend and Spring Boot / Data JPA backend.

---

## ![](https://img.shields.io/badge/📸_Application_Demo-8A2BE2?style=for-the-badge)

<div align="center">

<span style="color: #ffffff; background-color: #007ACC; padding: 5px 12px; border-radius: 4px; font-weight: bold;">Available Shifts & Dynamic Filtering</span>  
<br />
<img width="1917" height="986" alt="Available Shifts" src="https://github.com/user-attachments/assets/109a3981-fd7d-4543-b562-b974562df92b" />

<br /><br />

<span style="color: #ffffff; background-color: #28A745; padding: 5px 12px; border-radius: 4px; font-weight: bold;">Shift Selection & Conflict Alert</span>
<br />
<img width="1912" height="997" alt="Conflict Alert" src="https://github.com/user-attachments/assets/63a3a4cf-813c-4aae-8d58-65d07b264d5f" />

</div>

---

## ![](https://img.shields.io/badge/📐_Database_Architecture-E65100?style=for-the-badge)

The system models a **Many-to-Many (`@ManyToMany`)** relationship between `Shift` and `Schedule` entities. This design allows master shift slots to exist in a shared pool and be assigned to multiple user schedules without duplicating database rows.

```mermaid
erDiagram
    SHIFTS {
        Long id PK
        LocalDate date
        LocalTime start_time
        LocalTime end_time
        Double hours
        Boolean is_available
    }

    SCHEDULES {
        Long id PK
        Long user_id FK
    }

    USER_SCHEDULES {
        Long schedule_id FK
        Long shift_id FK
    }
SHIFTS ||--o{ USER_SCHEDULES : "included in"
SCHEDULES ||--o{ USER_SCHEDULES : "contains" 
```
---
## ![](https://img.shields.io/badge/🔗_API_Endpoints-007ACC?style=for-the-badge)

<details>
<summary><b>Click to expand Shift Endpoints (<code>/shifts</code>)</b></summary>

  <br />


| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/shifts` | Fetch all active shifts (supports optional `type` and `day` parameters) |
| `POST` | `/shifts` | Create a new master shift slot |
| `DELETE` | `/shifts/{shiftId}` | Remove a shift slot by ID |

</details>

<br />

<details>
<summary><b>Click to expand Schedule Endpoints (<code>/schedules</code>)</b></summary>

<br />

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/schedules/users/{userId}` | Get confirmed schedule and assigned shifts for a user |
| `POST` | `/schedules/users/{userId}/shifts/{shiftId}` | Claim and assign a shift to a user schedule |
| `DELETE` | `/schedules/users/{userId}/shifts/{shiftId}` | Drop a shift from a user schedule |

</details>

---

## ![](https://img.shields.io/badge/⚙️_Local_Setup_&_Installation-FF9900?style=for-the-badge)

### 📋 Prerequisites
* JDK 17 or higher
* Node.js (v18+) & npm
* MySQL Server running locally on port `3306`

### 🗄️ 1. Database Configuration
Open MySQL Workbench and execute:
```sql
CREATE DATABASE shift_planner_db;
```
### ☕ 2. Backend Setup
 Clone the repository and navigate to the backend directory:
   ```bash
   git clone [https://github.com/lakshmipcherukula-arch/ShiftPlanner.git](https://github.com/lakshmipcherukula-arch/ShiftPlanner.git)
   cd ShiftPlanner/shift-planner-backend

1.Create an .env file in the root backend directory:

Code snippet
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/shift_planner_db
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_mysql_password

2.Run the Spring Boot application:
./mvnw spring-boot:run
```
### ⚛️ 3. Frontend Setup

1. Open a new terminal and navigate to the frontend directory:
cd ShiftPlanner/shift-planner-frontend
2. Install dependencies and start the Vite development server:
npm install
npm run dev
3. Open http://localhost:5173 in your browser.
   

Lakshmi Prasanna Cherukula

GitHub: @lakshmipcherukula-arch

  
