# HealthPals

HealthPals is a personal health management web application with user/admin roles, health indicator tracking, health news, community interaction, and notifications.

This repository currently contains:
- `frontend`: Vue 2.7 + Vue CLI 5 + Element UI
- `backend`: Spring Boot 2.2 + MyBatis + MySQL

## Tech Stack

### Frontend
- Vue 2.7
- Vue Router 3
- Vue CLI Service 5
- Element UI
- Axios
- Sass

### Backend
- Spring Boot 2.2.4
- Spring MVC
- MyBatis
- MySQL
- JWT

### Database
- MySQL (`personal_health`)

## Key Features

- User registration/login, profile update, password reset
- Role-based pages (User/Admin)
- Health indicator records and visualization
- Health news browsing, tagging, comments, favorites
- Message/notification center
- Admin operations (users, news, health model configs)
- Google one-click sign-in/sign-up (requires OAuth configuration)

## Project Structure

```text
Personal-Health-Management-Web/
├── frontend/   # Vue app
├── backend/    # Spring Boot API
└── README.md
```

## Environment Configuration

### Frontend

Create `frontend/.env.development`:

```env
VUE_APP_GOOGLE_CLIENT_ID=your-google-oauth-client-id.apps.googleusercontent.com
```

Reference template: `frontend/.env.example`

### Backend

Configure `backend/src/main/resources/application.yml`:

- Database connection (`spring.datasource.*`)
- Google OAuth client ID:

```yml
google:
  oauth:
    client-id: "your-google-oauth-client-id.apps.googleusercontent.com"
```

> Frontend and backend Google Client ID should be the same.

## Run Locally

### 1) Start MySQL

Ensure database `personal_health` exists and credentials in `application.yml` are correct.

### 2) Start Backend

```bash
cd backend
mvn spring-boot:run
```

Default API base URL:

`http://localhost:21090/api/personal-heath/v1.0`

Quick check:

```bash
curl -i "http://localhost:21090/api/personal-heath/v1.0/user/auth"
```

### 3) Start Frontend

```bash
cd frontend
npm install
npm run dev
```

Open the local URL printed by Vue CLI (commonly `http://localhost:21092`).

## Build Commands

### Frontend

```bash
cd frontend
npm run build
```

### Backend

```bash
cd backend
mvn package -DskipTests
```
