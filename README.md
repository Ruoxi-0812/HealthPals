# HealthPals

Full-stack personal health web app: user/admin roles, health records & charts, health news & community, notifications,and AI health Chat.

**Stack:** Vue + Spring Boot + MyBatis + MySQL (`personal_health`)

## Features

- Register / login, profile, password
- User & admin dashboards
- Health indicators & ECharts
- News, tags, comments, favorites
- Messages / notifications
- health assistant

## Docker

```bash
cp .env.example .env   # set MYSQL_ROOT_PASSWORD; optional APP_AI_API_KEY
docker compose up --build
```

- App: http://localhost:8080  
- API: http://localhost:21090/api/personal-heath/v1.0  
- MySQL on host: `localhost:3307` (schema from `sql/personal_health.sql` on first run)

## Local dev

**Prerequisites:** Node, JDK, Maven, MySQL with DB imported:

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS personal_health CHARACTER SET utf8mb4;"
mysql -u root -p personal_health < sql/personal_health.sql
```

**Backend**

```bash
export SPRING_DATASOURCE_PASSWORD='your-mysql-password'
cd backend && mvn spring-boot:run
```

**Frontend**

```bash
cd frontend && npm install && npm run dev
```

Open http://localhost:21091 (proxies `/api` → backend).

**Optional env**

| Variable | Where |
|----------|--------|
| `SPRING_DATASOURCE_PASSWORD` | Backend (required locally) |
| `APP_AI_API_KEY` | Backend — health assistant |
| `VUE_APP_GOOGLE_CLIENT_ID` | `frontend/.env.development` — see `.env.example` |
| `google.oauth.client-id` | `backend/.../application.yml` |

## Build

```bash
cd frontend && npm run build
cd backend && mvn package -DskipTests
```
