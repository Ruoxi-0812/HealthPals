# Free deployment: Vercel + Render + Aiven MySQL

Status: configuration prepared locally; cloud services have not been created.

## 1. Aiven

Create a MySQL service explicitly on the Free plan, not a paid trial.
Use a dedicated new database (for example `healthpals`) for this deployment.
Keep credentials in provider environment settings, never in Git or chat.

Restore a verified backup if available. The repository's `sql/personal_health.sql`
is an old development dump, not a current AWS backup, and contains DROP TABLE
statements and sample accounts. Do not import it into an existing database or
publish its sample accounts unchanged. For a fresh deployment, use a clean
schema and register new accounts instead. Apply
`sql/migrations/20260922_health_submission.sql` after the base schema.

Use the actual Aiven host and port in a JDBC URL such as:

```text
jdbc:mysql://HOST:PORT/healthpals?sslMode=REQUIRED&characterEncoding=utf8&serverTimezone=UTC
```

`REQUIRED` enforces encryption but does not verify the server identity. For
certificate verification, configure a Java truststore with Aiven's CA and use
`sslMode=VERIFY_IDENTITY`. Do not copy the local default `useSSL=false` URL.

## 2. Render

Push the reviewed source and root `render.yaml` to the connected GitHub
repository. Create a Blueprint from that repository. Verify the resulting
web service is Free before creating it; no paid database or disk is included.

Enter SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME and
SPRING_DATASOURCE_PASSWORD when prompted. The Blueprint generates a JWT secret.
It uses the existing backend Dockerfile, port 10000, a small connection pool,
and bounded Java memory suitable as a starting point for the 512 MB instance.
Actual memory use still needs verification after startup.

Leave APP_AI_API_KEY unset to avoid paid AI API use. AI responses will remain
unavailable until a separately authorized provider is configured.

Verify the deployed `/api/personal-heath/v1.0/health` endpoint and database-backed
registration/login before switching the frontend.

## 3. Vercel

Once the real Render hostname is known, replace the AWS destination in
`frontend/vercel.json` with `https://ACTUAL-HOST.onrender.com/api/:path*` and
redeploy the existing Vercel project. Do not deploy the placeholder above.
Keep the existing Vercel hostname. Test registration, login, record submission,
retries, and admin access with newly created test accounts.

## Limitations to resolve

- Render Free sleeps after 15 minutes without traffic; cold starts may make
  the first API request fail or time out. Recheck after the service is awake.
- Local uploads on Render are ephemeral. Persistent image uploads require
  separate storage integration before this is a complete feature migration.
- Existing AWS data and uploaded files do not migrate automatically.
- Aiven Free currently provides 1 GB storage. Keep an independent backup.
- Neither provider's current free policy is a guarantee of permanent pricing.

References: https://render.com/docs/free,
https://render.com/docs/blueprint-spec,
https://aiven.io/docs/products/mysql/concepts/mysql-free-tier
