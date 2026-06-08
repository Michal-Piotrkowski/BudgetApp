# BudgetApp 💰

A comprehensive full-stack web application designed for managing personal finances, tracking expenses, and budgeting. 

## 👨‍💻 Author

**Michał Piotrkowski**  
*Applied Computer Science (3rd Year / Junior)*  
*Faculty of Mechanical Engineering*  
*PK Krakow University of Technology (Politechnika Krakowska)*  

---

## 🛠️ Tech Stack

### Frontend (`/client`)
- **Framework:** React 19 + TypeScript
- **Build Tool:** Vite
- **Routing:** React Router DOM
- **Form Handling:** React Hook Form
- **Styling:** Sass
- **HTTP Client:** Axios
- **Notifications:** React Toastify

### Backend (`/server`)
- **Framework:** Spring Boot 4
- **Language:** Java 21
- **API:** GraphQL & REST
- **Database:** MySQL & Spring Data JPA
- **Security:** Spring Security + JWT Authentication
- **Real-time:** WebSockets
- **Utils:** Lombok, Hibernate Validator

---

## 📁 Project Structure

The repository is organized into a monorepo setup containing both the frontend and backend:

- **`/client`** - The React frontend application.
- **`/server`** - The Spring Boot backend application.

---

## 🚀 Getting Started

### Prerequisites
- Node.js and npm (for frontend)
- Java 21 and Maven (for backend)
- MySQL Database
- Docker (optional, for running the database via `docker-compose.yml`)

### 1. Running the Backend
1. Navigate to the `server` directory:
   ```bash
   cd server
   ```
2. (Optional) Start the database using Docker Compose:
   ```bash
   docker-compose up -d
   ```
3. Run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```
   *The backend will typically start on `http://localhost:8080`.*

### 2. Running the Frontend
1. Navigate to the `client` directory:
   ```bash
   cd client
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the Vite development server:
   ```bash
   npm run dev
   ```
   *The frontend will run on `http://localhost:5174` as configured.*

---

## 📜 About
This project was developed as part of the coursework at the Cracow University of Technology (PK).
