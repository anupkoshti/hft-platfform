FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=docker


# Copy pom and download dependencies in a cached layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the actual source code
COPY src ./src

# Build the JAR
RUN mvn clean package -DskipTests

# -----------------------------
# 2. Runtime Stage
# -----------------------------
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the JAR from build stage
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
