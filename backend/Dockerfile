# Build executable JAR
FROM maven:3.9-eclipse-temurin-8 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -q package -DskipTests

# Run (needs SPRING_DATASOURCE_PASSWORD etc. at runtime)
FROM eclipse-temurin:8-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /app/target/personal-health-api-1.0-SNAPSHOT.jar app.jar
USER app
EXPOSE 21090
ENTRYPOINT ["java", "-jar", "app.jar"]
