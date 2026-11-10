<div align="center">

# E-Banking Microservices Platform

**Spring Boot · Spring Cloud · Angular 22 · Docker · Gemini AI · MCP**

An educational full-stack banking platform demonstrating **service discovery, centralized configuration, API routing, inter-service communication, fault tolerance, and AI-assisted interactions**.

[![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.2-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-22-DD0031?logo=angular&logoColor=white)](https://angular.dev/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)](https://docs.docker.com/compose/)
[![License](https://img.shields.io/badge/License-Not_specified-lightgrey)](#project-status-and-limitations)

[Architecture](#architecture) · [Services](#services-and-local-ports) · [Quick Start](#quick-start) · [Configuration](#configuration-and-security) · [Limitations](#project-status-and-limitations)

</div>

---

## Overview

This repository contains a modular E-Banking application developed as a **software engineering / microservices learning project**. It brings together backend services, an Angular web application, a centralized configuration repository, Docker Compose orchestration, and optional AI integrations.

### Key capabilities

- Customer and bank-account management through REST APIs
- **Spring Cloud Gateway** with service-discovery-based routing
- **Netflix Eureka** for service registration and discovery
- **Spring Cloud Config Server** for shared configuration
- **OpenFeign**, **Spring Cloud LoadBalancer**, and **Resilience4J** dependencies in the banking service
- **Angular 22** frontend served by **Nginx** in the containerized setup
- **Gemini-powered AI chatbot**, **MCP server**, and Telegram / Discord integrations
- **Docker Compose** with health checks and startup dependencies

## Architecture

```text
                       Browser
                          |
                          v
                 Angular 22 + Nginx
                    localhost:4200
                          |
                          v
                  Spring Cloud Gateway
                    localhost:8080
                          |
             +------------+-------------+
             |            |             |
        /customers/**  /accounts/**   /chat/**
             |            |             |
             v            v             v
       Customer Service  E-Bank      AI Chatbot
          :8081         Service        :8084
                         :8082           |
                                        +---- Google Gemini
                                        +---- MCP Server :8085
                                        +---- Telegram / Discord

     Eureka Discovery :8761  |  Config Server :8888
                Shared configuration repository
```

The diagram summarizes configured routes and components; it is not a guarantee that all integrations are operational in every environment.

## Services and local ports

| Component | Port | Responsibility |
| :--- | ---: | :--- |
| Angular + Nginx | 4200 | Browser-facing web application |
| Gateway Service | 8080 | API routing and service discovery |
| Customer Service | 8081 | Customer REST endpoints |
| E-Bank Service | 8082 | Banking and account endpoints |
| AI Chatbot Service | 8084 | Gemini-based assistant and bot integrations |
| MCP Server Service | 8085 | Model Context Protocol integration |
| Eureka Discovery | 8761 | Service registry and dashboard |
| Config Server | 8888 | Centralized Spring configuration |

**Gateway route definitions:** `/customers/**`, `/accounts/**`, and `/chat/**` are configured in `ebanking-config-repo/gateway-service.properties`.

## Technology stack

| Layer | Technologies |
| :--- | :--- |
| Backend | Java 17, Spring Boot, Spring Web MVC, Spring Data JPA, Maven |
| Microservices | Spring Cloud Gateway, Eureka, Spring Cloud Config, OpenFeign, LoadBalancer, Resilience4J |
| Frontend | Angular 22, TypeScript, HTML, CSS |
| AI & integrations | Spring AI, Google Gemini, MCP, Telegram, Discord (JDA) |
| Database | H2 in-memory database in the banking services |
| Infrastructure | Docker, Docker Compose, Nginx, Git |

The inspected Maven files use **Spring Boot 4.1.2**; some services specify different Spring Cloud BOM patch versions. See [Limitations](#project-status-and-limitations).

## Repository structure

```text
ebanking-microservices-spring-cloud/
├── ai-chatbot-service/
├── config-service/
├── customer-service/
├── discovery-service/
├── ebank-service/
├── ebanking-angular/
├── ebanking-config-repo/
├── ebanking-docker/
│   ├── compose.yaml
│   └── .env.example
├── gateway-service/
├── mcp-server-service/
├── .gitignore
└── README.md
```

## Quick start

### Prerequisites

- Git
- Docker Desktop or a compatible Docker Engine
- Docker Compose plugin
- Credentials for any optional AI/bot integrations you intend to enable

### 1. Clone

```bash
git clone https://github.com/mohamed-yawina/ebanking-microservices-spring-cloud.git
cd ebanking-microservices-spring-cloud
```

### 2. Configure environment variables

On Windows PowerShell:

```powershell
Copy-Item .\ebanking-docker\.env.example .\ebanking-docker\.env
```

On macOS/Linux:

```bash
cp ebanking-docker/.env.example ebanking-docker/.env
```

Edit `ebanking-docker/.env` and replace placeholders with **your own** keys and identifiers if using Gemini, Discord, or Telegram. Do not commit this file.

### 3. Validate the Compose configuration

```bash
docker compose --env-file ebanking-docker/.env -f ebanking-docker/compose.yaml config
```

### 4. Build and launch

```bash
docker compose -f ebanking-docker/compose.yaml up --build -d
```

### 5. Inspect status and logs

```bash
docker compose -f ebanking-docker/compose.yaml ps
docker compose -f ebanking-docker/compose.yaml logs --tail=100 gateway-service
```

### 6. Open local endpoints

- **Web app:** http://localhost:4200
- **Gateway:** http://localhost:8080
- **Eureka dashboard:** http://localhost:8761
- **Config Server:** http://localhost:8888

These addresses assume successful local startup with the published Compose port mappings.

### 7. Stop

```bash
docker compose -f ebanking-docker/compose.yaml down
```

> **Data persistence:** The inspected banking service configuration uses `jdbc:h2:mem:ebank-db` and `spring.jpa.hibernate.ddl-auto=create-drop`. Treat data as temporary: restarting the service can erase it.

## Configuration and security

- Configuration files are maintained in `ebanking-config-repo/` and served by Spring Cloud Config.
- The AI service uses environment variables from `ebanking-docker/.env`, based on `.env.example`.
- The repository's `.gitignore` excludes common `.env` files, build artifacts, IDE settings, and local secrets.
- **Never commit API keys, tokens, passwords, or production customer data.**
- This repository is a **demonstration**, not a production banking system. Production use would require a security review, authentication and authorization, HTTPS, managed secrets, persistent storage, and deployment hardening.

## Project status and limitations

The repository structure, Compose definitions, Maven dependencies, Angular package manifest, and selected configuration files were inspected to prepare this documentation. **A successful end-to-end build or runtime test has not been established by this README audit.**

Items to verify when running locally:

1. **Docker networking:** `ebanking-config-repo/ebank-service.properties` uses `http://localhost:8761/eureka/` for Eureka. Inside a container, `localhost` refers to that container, not the Eureka container. Confirm that the configuration resolves to the appropriate Docker service hostname.
2. **Service readiness:** Compose includes health checks and `depends_on` conditions. Check container health and logs if startup is blocked.
3. **Version alignment:** Inspected modules use Spring Boot 4.1.2, while Spring Cloud BOM patch versions differ between some modules. Verify dependency compatibility during builds.
4. **AI integrations:** Gemini, Telegram, and Discord require valid credentials and may not work with placeholder values.
5. **Data storage:** H2 in-memory databases are suitable for development and demos, not persistent banking records.
6. **Licensing:** No license is declared in this README; check repository licensing before reuse or redistribution.

## Suggested verification commands

For an individual Java module, from its directory:

```bash
./mvnw test
```

On Windows, use `mvnw.cmd test` if the Maven wrapper is present. Alternatively use an installed Maven distribution with `mvn test`.

For the Angular app:

```bash
cd ebanking-angular
npm ci
npm run build
npm test
```

These are **commands to run**, not claims that the tests have passed.

## Author

**Mohamed Yawina** — Software Engineering Student, EMSI (Morocco)

[GitHub Profile](https://github.com/mohamed-yawina) · [LinkedIn](https://www.linkedin.com/in/mohamed-yawina/)

---

<div align="center"><sub>Built for learning, architecture practice, and practical full-stack experimentation.</sub></div>
