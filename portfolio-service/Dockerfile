# -----------------------------
# 1. Build Stage
# -----------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=docker

# Build and install parent pom, then common module
COPY pom.xml ./
RUN mvn -N install

# Build and install common module
COPY common/pom.xml ./common/pom.xml
COPY common/src ./common/src
WORKDIR /app/common
RUN mvn clean install -DskipTests

# Now build portfolio-service
WORKDIR /app/portfolio-service
COPY portfolio-service/pom.xml .
RUN mvn dependency:go-offline -B

# Copy the actual source code
COPY portfolio-service/src ./src

# Build the JAR
RUN mvn clean package -DskipTests

# -----------------------------
# 2. Runtime Stage
# -----------------------------
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the JAR from build stage
COPY --from=build /app/portfolio-service/target/*.jar app.jar

EXPOSE 8084

# Best practice — JVM performance flags + restart-friendly
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
