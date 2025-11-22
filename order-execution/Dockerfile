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

# Now build order-execution service
WORKDIR /app/order-execution
COPY order-execution/pom.xml .
RUN mvn dependency:go-offline -B

# Copy the actual source code
COPY order-execution/src ./src

# Build the JAR
RUN mvn clean package -DskipTests

# -----------------------------
# 2. Runtime Stage
# -----------------------------
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the JAR from build stage
COPY --from=build /app/order-execution/target/*.jar app.jar

EXPOSE 8090

ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
