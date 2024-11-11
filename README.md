# GitShield - GitHub Repository Vulnerability Scanner

A full-stack web application that enables secure scanning of GitHub repositories for vulnerabilities, utilizing GitHub security tools such as Secret Scan, Code Scan, and Dependabot. This project supports multi-user access with Role-Based Access Control (RBAC), stores vulnerability data in Elasticsearch, and integrates Jira for automated issue tracking.

---

## Table of Contents
[Project Overview](#project-overview)
[Features](#features)
[Architecture](#architecture)
[Technology Stack](#technology-stack)
[Getting Started](#getting-started)
[Configuration](#configuration)
[Usage Guide](#usage-guide)
[License](#license)

---

## Project Overview

**GitShield** is a security-focused application designed for organizations and developers to monitor and manage vulnerabilities within their GitHub repositories. It leverages GitHub’s security tools to identify issues, stores data dynamically with Elasticsearch, and automates task creation in Jira to prioritize security fixes.

---

## Features

- **GitHub Security Integration**: Supports Secret Scan, Code Scan, and Dependabot to identify vulnerabilities.
- **Elasticsearch Storage**: Stores findings dynamically, creating custom indexes per organization for efficient querying and retrieval.
- **Role-Based Access Control (RBAC)**: Manages user access with secure permissions using an SQL database.
- **Kafka-Driven Processing**: Uses Kafka to handle data flow between services efficiently.
- **Automated Jira Integration**: Creates Jira tickets by vulnerability severity to streamline remediation workflows.

---

## Architecture

![Architecture Diagram](https://link-to-architecture-diagram-image)

The application follows a modular architecture with microservices that handle scanning, indexing, permissions, and ticketing independently. This setup ensures scalability, efficient processing, and secure data management.

### Key Components:
- **Frontend (React)**: Provides an intuitive user interface for scanning, monitoring vulnerabilities, and managing access.
- **Backend (Java Spring Boot)**: Manages API requests, connects to databases, and controls core application logic.
- **Data Processing (Kafka)**: Ensures reliable communication and data handling between services.
- **Data Storage**: Uses Elasticsearch for indexing findings and an SQL database for user roles and permissions.
- **Jira Integration**: Automatically creates and assigns issues in Jira based on vulnerability severity.

---

## Technology Stack

- **Frontend**: React
- **Backend**: Java, Spring Boot
- **Database**: Elasticsearch for scan data, SQL for RBAC
- **Data Streaming**: Kafka
- **Issue Tracking**: Jira integration

---

## Getting Started

### Prerequisites

[Java Development Kit (JDK) 11+](https://adoptopenjdk.net/)
[Node.js](https://nodejs.org/) and npm
[Kafka](https://kafka.apache.org/)
[Elasticsearch](https://www.elastic.co/elasticsearch/)
[SQL Database Setup] (e.g., MySQL, PostgreSQL)

### Installation

**Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/gitshield.git
   cd gitshield
```

**Backend Setup**
- Navigate to the backend folder:
```bash
cd backend
```
- Build the project with Maven:
```bash
mvn clean install
```
- Configure `application.properties` with database and Elasticsearch settings.
  
**Frontend Setup**
- Navigate to the `frontend` folder and install dependencies:
```bash
npm install
```
- Start the frontend:
```bash
npm start
```

**Start Kafka and Elasticsearch Services**
- Follow setup instructions for Kafka and Elasticsearch, ensuring they are running before starting the application.

  
### Configuration
- **Database Configuration:** In `backend/src/main/resources/application.properties`, configure your SQL and Elasticsearch connection details.
- **Kafka Topics:** Create required Kafka topics for producers and consumers.
- **Jira Integration:** Set Jira API credentials and URL in the configuration file under the jira section to enable ticket automation.
- **GitHub API:** To enable repository scanning, add GitHub API credentials in application.properties.
  
### Usage Guide
- **User Authentication:** Login using your credentials. New users can sign up if permitted by the admin.
- **Link GitHub Repositories:**
  - Navigate to the "Repositories" tab and link repositories.
  - Start a scan by clicking the "Scan" button.
- **View Vulnerabilities:**
  - Results are displayed in the dashboard, categorized by severity.
  - Click on each finding for detailed information and remediation steps.
- **Issue Tracking:**
  - Based on the severity, Jira tickets are automatically created.
  - View ticket status and details directly from the application.
- **Admin Dashboard:**
  - Manage user roles and access permissions via the Admin dashboard.
