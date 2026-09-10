# Build stage: compile the Spring Boot app
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy the Maven files first for better caching
COPY pom.xml .
COPY .mvn .mvn
RUN mvn dependency:go-offline

# Copy the rest of the source
COPY src src
RUN mvn clean package -DskipTests

# Production stage: run the app
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the app port (9020)
EXPOSE 9020

# Start the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
