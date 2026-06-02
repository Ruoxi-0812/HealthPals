# HealthPals

![CI](https://github.com/Ruoxi-0812/HealthPals/actions/workflows/ci.yml/badge.svg)

HealthPals is a full-stack personal health management application. Users can track health records, view health trends, read curated health articles, save favorites, join discussions, receive notifications, and use an AI health assistant. Admin users can manage users, articles, categories, comments, messages, health metric models, and health records.

Application: [https://health-pals.vercel.app](https://health-pals.vercel.app)

## Architecture

HealthPals is organized around a Vue frontend and a Spring Boot REST API with feature modules for user health data, article reading, comments, notifications, admin management, uploads, and AI assistance.

```mermaid
flowchart TB
  User((User))
  Admin((Admin))
  Frontend["frontend\nVue"]
  API["backend API\nSpring Boot"]
  Auth["auth"]
  Health["health records"]
  News["news"]
  Community["comments\nmessages"]
  AdminConsole["admin console"]
  Assistant["AI assistant"]
  Uploads["media uploads"]
  DB[("AWS RDS MySQL")]
  OAuth["Google OAuth"]
  AI["AI API"]
  Storage["ECS task storage"]
  Secrets["Secrets Manager"]

  User -->|HTTP| Frontend
  Admin -->|HTTP| Frontend
  Frontend -->|REST API| API

  API --> Auth
  API --> Health
  API --> News
  API --> Community
  API --> AdminConsole
  API --> Assistant
  API --> Uploads

  Auth --> OAuth
  Auth --> DB
  Health --> DB
  News --> DB
  Community --> DB
  AdminConsole --> DB
  Assistant --> AI
  Uploads --> Storage
  API --> Secrets

  classDef frontend fill:#eadfe4,stroke:#b9b9b9,color:#111,font-weight:bold;
  classDef backend fill:#f7f0d8,stroke:#b9b9b9,color:#111,font-weight:bold;
  classDef service fill:#ffffff,stroke:#b9b9b9,color:#444;
  classDef datastore fill:#ffffff,stroke:#999,color:#444;
  class Frontend frontend;
  class API backend;
  class Auth,Health,News,Community,AdminConsole,Assistant,Uploads,OAuth,AI,Storage,Secrets service;
  class DB datastore;
```

| Service | Technology | Description |
| --- | --- | --- |
| frontend | Vue | User and admin web interface. |
| backend | Spring Boot | REST API for auth, health records, news, comments, messages, uploads, and AI assistant. |
| database | AWS RDS MySQL | Stores users, health data, articles, comments, messages, and configuration data. |
| cloud | Vercel + AWS | Vercel frontend, ECS Fargate backend, ECR images, Secrets Manager, and GitHub Actions CI/CD. |

## Screenshots

### User Home Page

![User home page](docs/screenshots/user-home.png)

### Admin Home Page

![Admin home page](docs/screenshots/admin-home.png)

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
