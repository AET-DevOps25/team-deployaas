# ---- Stage 1: Build the application ----
FROM eclipse-temurin:24-jdk AS builder

# Set working directory inside container
WORKDIR /app

# Copy project files
COPY . .

# Ensure Gradle wrapper is executable
RUN chmod +x ./gradlew

# Clean and build the application JAR
RUN ./gradlew clean build -x test --no-daemon

# ---- Stage 2: Create a minimal runtime image ----
FROM eclipse-temurin:24-jre

# Set working directory
WORKDIR /app

# Copy built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port your service runs on
EXPOSE 8083

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
