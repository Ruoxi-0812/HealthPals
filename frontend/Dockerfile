# Build Vue SPA
FROM node:18-alpine AS build
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
ARG VUE_APP_API_BASE_URL=/api/personal-heath/v1.0
ARG VUE_APP_GOOGLE_CLIENT_ID=
ENV VUE_APP_API_BASE_URL=$VUE_APP_API_BASE_URL
ENV VUE_APP_GOOGLE_CLIENT_ID=$VUE_APP_GOOGLE_CLIENT_ID
RUN npm run build

# Serve static files + reverse-proxy API to backend (see nginx/default.conf)
FROM nginx:1.25-alpine
COPY --from=build /app/dist /usr/share/nginx/html
ENV BACKEND_UPSTREAM=backend:21090
COPY nginx/default.conf.template /etc/nginx/templates/default.conf.template
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
