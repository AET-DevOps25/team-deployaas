# ---- Stage 1: Build the application ----
FROM eclipse-temurin:24-jdk AS builder

WORKDIR /app

# Copy project files
COPY . .

# Ensure Gradle wrapper is executable
RUN chmod +x ./gradlew

# Clean and build the application
RUN ./gradlew clean build -x test --no-daemon

# ---- Stage 2: Create the runtime container ----
FROM eclipse-temurin:24-jre

WORKDIR /app

# Copy only the built JAR file
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the service port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
