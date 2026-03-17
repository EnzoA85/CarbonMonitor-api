# CarbonMonitor API

REST API for calculating the carbon footprint of physical sites. Part of the **Hackathon Capgemini 2026** — *Calculer l'empreinte carbone d'un site physique*.

## Tech Stack

- **Java 17** · **Spring Boot 3.2**
- **PostgreSQL** · **Spring Data JPA**
- **JWT** authentication
- **SpringDoc / Swagger** for API documentation

## Features

- **Authentication**: Register, login, JWT-based sessions
- **Sites**: CRUD for physical sites (name, address, surface, parking, employees, energy)
- **Materials**: 10 Base Carbone® materials seeded on startup, custom materials via API
- **Carbon calculation**: Construction (materials) + exploitation (energy, parking, etc.)
- **KPIs**: CO₂ total, CO₂/m², CO₂/employee

## Quick Start

### With Docker

```bash
docker-compose up -d postgres capcarbon-api
```

API: `http://localhost:8080/api`  
Swagger UI: `http://localhost:8080/api/swagger-ui.html`

### Local Development

1. **PostgreSQL** running on `localhost:5432` with database `capcarbon`, user `capcarbon`, password `capcarbon`.

2. **Run the API**:

```bash
cd capcarbon-api
./mvnw spring-boot:run
```

## Configuration

| Variable | Default | Description |
|----------|---------|-------------|
| `server.port` | 8080 | Server port |
| `server.servlet.context-path` | /api | API base path |
| `spring.datasource.url` | jdbc:postgresql://localhost:5432/capcarbon | Database URL |

For Docker, use profile `docker` (see `application-docker.yml`).

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/auth/register` | Register user |
| POST | `/auth/login` | Login (returns JWT) |
| GET | `/auth/me` | Current user (requires auth) |
| GET | `/sites` | List sites |
| POST | `/sites` | Create site |
| GET | `/sites/{id}` | Get site |
| PUT | `/sites/{id}` | Update site |
| DELETE | `/sites/{id}` | Delete site |
| GET | `/sites/{id}/materials` | Get site materials |
| POST | `/sites/{id}/materials` | Add material to site |
| POST | `/sites/{id}/calculate` | Run carbon calculation |
| GET | `/sites/{id}/report` | Get carbon report |
| GET | `/materials` | List materials (Base Carbone®) |
| POST | `/materials` | Create custom material |

## Base Carbone® Materials

On first startup, 10 construction materials are seeded from [Base Carbone® ADEME](https://data.ademe.fr/datasets/base-carboner/full):

Béton, Acier, Verre, Bois, Aluminium, Cuivre, PVC, Plâtre, Terre cuite, Chanvre.

## License

Internal use — Hackathon Capgemini 2026.
