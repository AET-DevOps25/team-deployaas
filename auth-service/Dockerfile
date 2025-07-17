# ---- Stage 1: Build the application ----
FROM eclipse-temurin:24-jdk AS builder

# Set working directory inside container
WORKDIR /app

# Copy Gradle wrapper and configuration first to leverage Docker cache
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle* .
COPY settings.gradle* .

# Pre-download Gradle dependencies to prevent timeouts later
RUN chmod +x gradlew && ./gradlew --no-daemon build -x test || true

# Now copy the full project
COPY . .

# Build the application (will reuse downloaded Gradle distribution)
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
