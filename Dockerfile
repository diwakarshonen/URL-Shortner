# ---------- Build Stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Download dependencies and build
RUN mvn clean package -DskipTests

# ---------- Runtime Stage ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the generated JAR
COPY --from=build /app/target/*.jar app.jar

# Expose application port
EXPOSE 9090

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]