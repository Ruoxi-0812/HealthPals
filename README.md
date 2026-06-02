# HealthPals

![CI](https://github.com/Ruoxi-0812/HealthPals/actions/workflows/ci.yml/badge.svg)

HealthPals is a Vue + Spring Boot personal health app for health records, curated articles, comments, notifications, admin management, and an AI health assistant.

Live demo: [https://health-pals.vercel.app](https://health-pals.vercel.app)

## Architecture

HealthPals is organized around a Vue frontend and a Spring Boot REST API with feature modules for user health data, article reading, comments, notifications, admin management, uploads, and AI assistance.

```mermaid
flowchart TB
  User((User))
  Admin((Admin))
  Frontend["frontend\nVue"]
  Backend["backend REST API\nSpring Boot"]

  subgraph Features["Feature Modules"]
    direction TB
    UserFeatures["User Features\nhealth records + charts\nhealth news + saved articles\ncomments + messages\nAI health assistant"]
    AdminFeatures["Admin Features\nuser management\nnews + category management\nhealth model + record management\ncomment + message management"]
    PlatformServices["Platform Services\nGoogle OAuth + password login\nmedia uploads\nsecrets"]
  end

  subgraph Services["Data & External Services"]
    direction TB
    DB[("database\nAWS RDS MySQL")]
    AI["AI API"]
    OAuth["Google OAuth"]
    Media["ECS task storage"]
    Secrets["AWS Secrets Manager"]
  end

  User -->|HTTP| Frontend
  Admin -->|HTTP| Frontend
  Frontend -->|REST API| Backend

  Backend --> UserFeatures
  Backend --> AdminFeatures
  Backend --> PlatformServices

  UserFeatures --> DB
  AdminFeatures --> DB
  PlatformServices --> DB
  UserFeatures --> AI
  PlatformServices --> OAuth
  PlatformServices --> Media
  PlatformServices --> Secrets

  classDef frontend fill:#eadfe4,stroke:#b9b9b9,color:#111,font-weight:bold;
  classDef backend fill:#f7f0d8,stroke:#b9b9b9,color:#111,font-weight:bold;
  classDef service fill:#ffffff,stroke:#b9b9b9,color:#444;
  classDef datastore fill:#ffffff,stroke:#999,color:#444;
  class Frontend frontend;
  class Backend backend;
  class UserFeatures,AdminFeatures,PlatformServices,OAuth,AI,Media,Secrets service;
  class DB datastore;
```

| Service | Technology | Description |
| --- | --- | --- |
| frontend | Vue | User and admin web interface. |
| backend | Spring Boot | REST API for auth, health records, news, comments, messages, uploads, and AI assistant. |
| database | AWS RDS MySQL | Stores users, health data, articles, comments, messages, and configuration data. |
| cloud | Vercel + AWS | Vercel frontend, ECS Fargate backend, ECR images, Secrets Manager, and GitHub Actions CI/CD. |

## Quickstart

```bash
git clone https://github.com/Ruoxi-0812/HealthPals.git
cd HealthPals
cp .env.example .env
docker compose up --build
```

Open the app:

```text
http://localhost:8080
```

## Local Development

Backend:

```bash
mysql -u root -p personal_health < sql/personal_health.sql
cd backend
mvn spring-boot:run
```

Frontend:

```bash
cd frontend
npm install
npm run dev
```

## Deployment

- Frontend: Vercel
- Backend: AWS ECS Fargate
- Database: AWS RDS MySQL
- Images: AWS ECR
- Secrets: AWS Secrets Manager
- CI/CD: GitHub Actions
- Auth: Google OAuth + username/password login

Uploaded media is stored on the ECS task filesystem for the current demo deployment. For production, media uploads should be migrated to persistent object storage such as AWS S3 or Cloudinary.

## Documentation

- [Deployment plan](docs/deployment.md)
- [Docker Compose](docker-compose.yml)
- [GitHub Actions CI](.github/workflows/ci.yml)
- [Publish Docker images to ECR](.github/workflows/publish-ecr.yml)
