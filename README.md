# logistics-platform

![Status](https://img.shields.io/badge/status-active-brightgreen)
![Build & Tests](https://github.com/manos-grigorakis/logistics-platform/actions/workflows/build-and-test.yaml/badge.svg)
![Coverage](https://github.com/manos-grigorakis/logistics-platform/blob/main/.github/badges/jacoco.svg?raw=true)
![Branches Coverage](https://github.com/manos-grigorakis/logistics-platform/blob/main/.github/badges/branches.svg?raw=true)
![Latest Release](https://img.shields.io/github/v/release/manos-grigorakis/logistics-platform)

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

4.  Install Front-end dependencies

    ```bash
    cd frontend && npm install
    ```

5.  Start Front-end development server

    Run from the `frontend/` directory

    ```bash
    ng serve
    ```

6.  Start Backend server
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

## API Documentation

Interactive API documentation available at:
`http://localhost:8080/swagger-ui.html`
