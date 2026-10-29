# E-Banking Microservices Platform

A full-stack E-Banking application built with **Spring Boot, Spring Cloud, Angular 22, Docker, and AI integrations**.

The project demonstrates a microservices architecture with centralized configuration, service discovery, API routing, banking services, and an AI-powered chatbot.

## Technology Stack

**Backend**
- Java 17
- Spring Boot
- Spring Cloud Gateway
- Netflix Eureka
- Spring Cloud Config
- Maven
- REST APIs

**Frontend**
- Angular 22
- TypeScript
- HTML and CSS
- Nginx

**AI & Integrations**
- Google Gemini API
- Model Context Protocol (MCP)
- Telegram Bot
- Discord Bot

**DevOps**
- Docker
- Docker Compose
- Multi-stage Docker builds
- Git and GitHub

## Microservices Architecture

```text
                  Angular Frontend
                   localhost:4200
                         |
                         v
                  API Gateway
                   localhost:8080
                         |
             +-----------+-----------+
             |                       |
             v                       v
      Customer Service         E-Bank Service
       localhost:8081           localhost:8082

                AI Chatbot Service
                  localhost:8084
                         |
               +---------+---------+
               |         |         |
             Gemini     MCP    Telegram/Discord
                        Server
                    localhost:8085

Infrastructure:
- Eureka Discovery Service: localhost:8761
- Spring Cloud Config Server: localhost:8888
```

*This diagram provides a simplified overview of the application components.*

## Services and Ports

| Service | Port | Description |
|---|---|---|
| Angular | 4200 | Web application |
| API Gateway | 8080 | API entry point |
| Discovery Service | 8761 | Eureka service registry |
| Config Service | 8888 | Centralized configuration |
| Customer Service | 8081 | Customer management |
| E-Bank Service | 8082 | Banking operations |
| AI Chatbot Service | 8084 | AI assistant |
| MCP Server Service | 8085 | MCP integration |

## Project Structure

```text
ebanking-microservices-spring-cloud/
├── discovery-service/
├── config-service/
├── customer-service/
├── ebank-service/
├── gateway-service/
├── mcp-server-service/
├── ai-chatbot-service/
├── ebanking-angular/
├── ebanking-config-repo/
├── ebanking-docker/
│   ├── compose.yaml
│   └── .env.example
└── README.md
```

## Getting Started

### Prerequisites

- Git
- Docker Desktop
- Docker Compose

### 1. Clone the Repository

```bash
git clone https://github.com/mohamed-yawina/ebanking-microservices-spring-cloud.git
cd ebanking-microservices-spring-cloud
```

### 2. Configure Environment Variables

Create a local environment file from the provided template.

On Windows PowerShell:

```powershell
Copy-Item .\ebanking-docker\.env.example .\ebanking-docker\.env
```

Update `.env` with your own credentials and configuration values for the AI and bot integrations.

**Never commit `.env` or real API keys to GitHub.**

### 3. Build and Start the Application

```bash
docker compose -f ebanking-docker/compose.yaml up --build -d
```

Docker Compose builds the Java services and Angular frontend using their respective multi-stage Dockerfiles.

### 4. Access the Application

| Application | URL |
|---|---|
| Angular Frontend | http://localhost:4200 |
| API Gateway | http://localhost:8080 |
| Eureka Dashboard | http://localhost:8761 |
| Config Server | http://localhost:8888 |

### 5. Stop the Application

```bash
docker compose -f ebanking-docker/compose.yaml down
```

## Main Features

- Customer management
- Bank account management
- Microservices registration and discovery
- Centralized configuration
- API Gateway routing
- Angular dashboard
- AI chatbot powered by Google Gemini
- MCP server integration
- Telegram and Discord bot integrations
- Containerized deployment with Docker Compose

## Security and Configuration

Sensitive credentials are supplied through environment variables rather than committed source files.

The repository includes `.env.example` with placeholder values. Actual credentials must be configured locally.

For production deployment, additional security measures such as authentication, authorization, HTTPS, and secure secret management should be configured and verified.

## Build and Deployment

Each Spring Boot microservice uses a multi-stage Dockerfile to build the application with Maven and run it with Java 17.

The Angular application uses Node.js 24 for compilation and Nginx to serve the production build.

## Author

**Mohamed Yawina**

Software Engineering Student — EMSI, Morocco

**GitHub:** https://github.com/mohamed-yawina