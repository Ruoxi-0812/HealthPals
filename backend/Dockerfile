# Build executable JAR
FROM maven:3.9-eclipse-temurin-8 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -q package -DskipTests

# Run (needs SPRING_DATASOURCE_PASSWORD etc. at runtime)
FROM eclipse-temurin:8-jre-jammy
WORKDIR /app
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system app \
    && useradd --system --gid app app \
    && mkdir -p /app/uploads \
    && chown -R app:app /app/uploads
COPY --from=build /app/target/personal-health-api-1.0-SNAPSHOT.jar app.jar
ENV APP_UPLOAD_DIR=/app/uploads
USER app
EXPOSE 21090
ENTRYPOINT ["java", "-jar", "app.jar"]
