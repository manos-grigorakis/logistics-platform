# Logistics Platform

![Status](https://img.shields.io/badge/status-active-brightgreen)
![Coverage](https://github.com/manos-grigorakis/logistics-platform/blob/main/.github/badges/jacoco.svg?raw=true)
![Branches Coverage](https://github.com/manos-grigorakis/logistics-platform/blob/main/.github/badges/branches.svg?raw=true)
![Issues](https://img.shields.io/github/issues/manos-grigorakis/logistics-platform)
![Open PRs](https://img.shields.io/github/issues-pr/manos-grigorakis/logistics-platform)
![Build & Tests](https://github.com/manos-grigorakis/logistics-platform/actions/workflows/build-and-test.yaml/badge.svg)
![Latest Release](https://img.shields.io/github/v/release/manos-grigorakis/logistics-platform)

![Created At](https://img.shields.io/github/created-at/manos-grigorakis/logistics-platform?color=007ec6)
![Commits/month](https://img.shields.io/github/commit-activity/m/manos-grigorakis/logistics-platform)
![Last Commit](https://img.shields.io/github/last-commit/manos-grigorakis/logistics-platform)

## Overview

Logistics Platform is a management platform for small to medium businesses aiming to simplify daily operations such as customer quote generation, shipment and fleet management, CMR document generation and payment reconciliation. The application follows a monolithic architecture with a Domain-Driven Design (DDD) structure.

## API Documentation

- [Public API Documentation](https://manos-grigorakis.github.io/logistics-platform/api/)
- Local Swagger UI: `http://localhost:8080/swagger-ui.html`

### Core Features

- User Authentication & Role-Based Access Control (RBAC)
- Dashboard Analytics & Business Metrics
- Customer & Supplier Management
- Fleet & Vehicle Management
- Shipment Management
- Quote Generation
- CMR Document Generation & Signature Verification
- Payment Reconciliation & Invoice Matching
- Audit Logging
- Multi-language Support (EN / GR)

## Tech Stack

| **Technology**               | **Usage**                  |
| ---------------------------- | -------------------------- |
| Angular                      | Frontend SPA               |
| Tailwind CSS                 | UI Styling                 |
| Spring Boot                  | Backend REST API           |
| OpenAPI / Swagger UI / ReDoc | API Documentation          |
| Flyway                       | Database Migration         |
| MariaDB                      | Relational Database        |
| MinIO                        | S3-compatible File Storage |
| Docker / Docker Compose      | Containerization           |

## Prerequisites

**Backend**

- Java 17+
- Maven
- MariaDB 11.4

**Frontend**

- Node.js 18+
- Angular 20
- Angular CLI 20

**Infrastructure**

- Docker & Docker Compose
- MinIO (S3-compatible storage)

## Setup

### Development

1.  Clone the repo

    ```bash
    git clone https://github.com/manos-grigorakis/logistics-platform.git
    ```

2.  Copy and configure environment variables

    ```bash
    cp .env.example .env
    cp frontend/.env.example frontend/.env
    cp backend/.env.example backend/.env
    ```

3.  Start infrastructure services

    ```bash
    docker compose -f docker-compose.dev.yaml up -d
    ```

4.  Install frontend dependencies

    ```bash
    cd frontend && npm install
    ```

5.  Start frontend development server

    Run from the `frontend/` directory

    ```bash
    ng serve
    ```

6.  Start backend server
    1.  With Maven installed

        ```bash
        cd backend && mvn spring-boot:run
        ```

    2.  Without Maven

        ```bash
        cd backend && ./mvnw spring-boot:run
        ```

    3.  With [IntelliJ IDEA](https://www.jetbrains.com/idea/) \
        Open the `backend/` directory in IntelliJ IDEA (The run configuration loads automatically).

### Production

1. Copy and configure environment variables

   ```bash
   cp .env.example .env.prod
   cp frontend/.env.example frontend/.env.prod
   cp backend/.env.example backend/.env.prod
   ```

2. Start all services

   > Update the images tags (backend & frontend ) in `docker-compose.prod.yaml` before running.

   ```bash
   docker compose -f docker-compose.prod.yaml up -d
   ```

## Screenshots

### Dashboard

![Dashboard](/docs/screenshots/dashboard.png)

### Customer Profile

![Customer Profile](/docs/screenshots/customer-profile.png)

### Shipments Table

![Shipments Table](/docs/screenshots/shipments-table.png)

### Shipment View

![Shipment View](/docs/screenshots/shipment-view.png)

### Payment Reconciliation

![Payment Reconciliation](/docs/screenshots/payment-reconciliation.png)
