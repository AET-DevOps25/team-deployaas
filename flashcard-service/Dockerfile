# Stage 1: Build the application
FROM eclipse-temurin:24-jdk AS builder

WORKDIR /app
COPY . .

# Ensure Gradle wrapper is executable
RUN chmod +x ./gradlew

# Clean + build the JAR without running tests
RUN ./gradlew clean build -x test --no-daemon

# Stage 2: Run the application
FROM eclipse-temurin:24-jre

WORKDIR /app

# Copy only the built JAR (avoid copying test/build stuff)
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
