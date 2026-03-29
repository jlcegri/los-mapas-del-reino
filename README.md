<div align='center'>

<img src='/frontend/src/static/images/LMDR.jpg'>

# Los Mapas del Reino

> **Portfolio mirror repository**  
> This repository is a mirrored snapshot of the project used for portfolio purposes. See the **Full Development History** section below for the original repository with the complete commit timeline.

![Java](https://img.shields.io/badge/Java-17-informational)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.3-success)
![React](https://img.shields.io/badge/React-18-blue)
![Maven](https://img.shields.io/badge/Maven-Build-orange)
![Database](https://img.shields.io/badge/Database-H2%20%7C%20MySQL-lightgrey)

</div>

## Overview

**Los Mapas del Reino** is an academic full-stack web application based on a multiplayer board game for **2 to 4 players**. The system provides player and administrator flows, match lifecycle management, board creation, score calculation, and gameplay-related interactions through a **Spring Boot + React** architecture.

The project was developed in the context of the **DP1 (Diseño y Pruebas 1)** course and is intended to showcase software engineering practices such as layered architecture, REST API design, authentication, testing, documentation, and iterative refactoring.

## Table of Contents

- [Overview](#overview)
- [Team](#team)
- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Running the Application](#running-the-application)
- [Database Configuration](#database-configuration)
- [Testing](#testing)
- [Documentation](#documentation)
- [Full Development History](#full-development-history)
- [Notes](#notes)

## Team

This project was developed collaboratively as a university team project with colleagues from the University of Seville.

Thanks to Miguel, Francis and Carlos for being such great teammates throughout the development of the project.

## Key Features

### User and Access Management
- User registration and login
- Role-based access control for **players** and **administrators**
- Profile viewing and profile editing
- Administrative user management (create, update, delete, and inspect users)
- JWT-based authentication flow

### Match Management
- Create matches as a player
- Join waiting matches
- View your own matches
- Leave or delete matches
- Admin views for created, started, and finished matches
- Multiple match modes, including **standard**, **slow**, and **fast**

### Gameplay Features
- Board creation for each player in a match
- Dice-based match updates
- Territory selection and board progression
- Score calculation and winner visualization
- Access protection so players cannot open another player's board by manually changing URLs

### Engineering and Quality
- Backend and frontend test coverage
- Structured technical documentation under `docs/`
- Refactoring work focused on cleaner separation of responsibilities between controllers, services, and repositories

## Tech Stack

### Backend
- **Java 17**
- **Spring Boot 3.3.3**
- **Spring Data JPA**
- **Spring Security**
- **Spring Validation**
- **Spring Actuator**
- **Swagger / Springdoc OpenAPI**
- **JWT** authentication
- **Lombok**
- **MapStruct**
- **Maven**

### Frontend
- **React 18**
- **Create React App**
- **React Router**
- **Bootstrap / Reactstrap**
- **React Big Calendar**
- **React Hexgrid**
- **FilePond**
- **Swagger UI React**

### Database
- **H2 in-memory database** by default
- **MySQL** optional profile

### Testing
- **JUnit** for backend tests
- **Jest** and React testing utilities for frontend tests
- **JaCoCo** for backend coverage reports

## Project Structure

```text
DP1-2024-2025--l6-02-main/
├── .mvn/
├── docs/
│   ├── deliverables/
│   ├── diagrams/
│   └── Learning Contract.pdf
├── frontend/
│   ├── public/
│   ├── src/
│   ├── package.json
│   └── README.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   ├── resources/
│   │   └── webapp/
│   └── test/
├── docker-compose.yml
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## Getting Started

### Prerequisites

Install the following tools before running the project:

- **JDK 17**
- **Node.js 18.x**
- **npm 8.x**
- **Git**
- **Maven Wrapper** (already included in the repository)

Optional but recommended:

- **MySQL** if you want to run the MySQL profile
- **IntelliJ IDEA**, **Spring Tools Suite**, **VS Code**, or another Java/React-friendly IDE

## Running the Application

### 1. Clone the repository

```bash
git clone https://github.com/jlcegri/DP1-2024-2025--l6-02-main.git
cd DP1-2024-2025--l6-02-main
```

### 2. Run the backend

Using Maven Wrapper:

```bash
./mvnw spring-boot:run
```

The backend will be available at:

```text
http://localhost:8080
```

Swagger UI is typically available at:

```text
http://localhost:8080/swagger-ui/index.html
```

### 3. Run the frontend

In a second terminal:

```bash
cd frontend
npm install
npm start
```

The frontend will be available at:

```text
http://localhost:3000
```

The frontend is configured with a proxy to the backend running on `http://localhost:8080`.

## Database Configuration

### Default setup: H2

The project uses an **H2 in-memory database** by default, populated at startup with sample data.

Relevant defaults:

- Database name: `testdb`
- H2 console enabled
- Automatic schema creation and teardown (`create-drop`)

This makes the project easy to run locally without any additional database installation.

### Optional setup: MySQL

A MySQL-specific configuration is also included:

```properties
spring.datasource.url=jdbc:mysql://localhost/petclinic
spring.datasource.username=petclinic
spring.datasource.password=petclinic
```

If you want to use MySQL instead of H2, review and adapt `application-mysql.properties` to your local environment.

### Seed data

The repository includes a `data.sql` file with sample users, players, and matches to simplify local testing and demonstration.

## Testing

### Backend tests

Run backend tests with:

```bash
./mvnw test
```

Generate coverage-related build steps with:

```bash
./mvnw verify
```

### Frontend tests

From the `frontend/` directory:

```bash
npm test
```

### Testing scope

The project includes:

- Backend unit tests
- Frontend unit tests
- UI-related frontend tests
- Integration tests for controllers and API flows

## Documentation

The repository contains technical and academic documentation in the `docs/` folder, including:

- **System Requirements Analysis**
- **System Design**
- **Test Plan**
- **Diagrams and supporting documentation**
- **Learning Contract**

This documentation provides additional context on requirements, architecture decisions, refactoring work, and validation strategy.

## Full Development History

This repository contains a snapshot of the final state of the project.

> Due to repository ownership constraints, the full commit history is maintained in a separate repository.

The complete development history can be found [here](https://github.com/gii-is-DP1/DP1-2024-2025--l6-02).

This approach preserves the full development process in case the original repository becomes unavailable, restricted, or private.

---

## Notes

This project is presented here as part of a personal portfolio. Its purpose is to showcase:

- Full-stack development with **Spring Boot** and **React**
- REST API design and role-based security
- Database-backed application development
- Software architecture and refactoring practices
- Automated testing and technical documentation

