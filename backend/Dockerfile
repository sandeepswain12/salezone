# =========================
# STAGE 1 - BUILD
# =========================
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom first (better caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build jar
RUN mvn clean package -DskipTests


# =========================
# STAGE 2 - RUN
# =========================
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Create folder for images (important for your project)
RUN mkdir -p /app/images/users
RUN mkdir -p /app/images/products

# Copy jar from builder
COPY --from=builder /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run app
ENTRYPOINT ["java", "-jar", "app.jar"]