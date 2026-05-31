# Deployment Plan

HealthPals is containerized as three local services:

- `frontend`: Vue app served by nginx
- `backend`: Spring Boot API
- `mysql`: local development database

For AWS, use managed infrastructure instead of running the database as a container:

- ECR: stores `healthpals-frontend` and `healthpals-backend` images
- ECS Fargate: runs frontend and backend tasks
- RDS MySQL: production database
- ALB: routes HTTPS traffic to frontend/backend
- Secrets Manager: stores database password, AI API key, and Google OAuth client id

## Local Docker

```bash
MYSQL_ROOT_PASSWORD='your-db-password' \
GOOGLE_OAUTH_CLIENT_ID='your-google-client-id.apps.googleusercontent.com' \
docker compose up --build -d
```

Open:

```text
http://localhost:8080
```

Check service health:

```bash
MYSQL_ROOT_PASSWORD='your-db-password' \
GOOGLE_OAUTH_CLIENT_ID='your-google-client-id.apps.googleusercontent.com' \
docker compose ps
```

## GitHub Actions

`CI` runs on pushes and pull requests:

- backend Maven package
- frontend lint and build
- Docker Compose config validation
- backend Docker image build
- frontend Docker image build

`Publish Docker Images to ECR` builds and pushes production images to Amazon ECR.

## Required GitHub Configuration

Create these repository variables:

```text
AWS_REGION
ECR_BACKEND_REPOSITORY
ECR_FRONTEND_REPOSITORY
```

Create these repository secrets:

```text
AWS_ROLE_TO_ASSUME
GOOGLE_OAUTH_CLIENT_ID
```

Use GitHub OIDC for AWS authentication instead of long-lived AWS access keys.

## AWS Checklist

1. Create two ECR repositories:
   - `healthpals-backend`
   - `healthpals-frontend`
2. Create a GitHub OIDC IAM role that can push to those ECR repositories.
3. Run the `Publish Docker Images to ECR` workflow.
4. Create RDS MySQL and import `sql/personal_health.sql`.
5. Create ECS Fargate task definitions for frontend and backend.
6. Store runtime secrets in AWS Secrets Manager.
7. Create an ALB with:
   - `/api/*` routed to backend
   - `/` routed to frontend
