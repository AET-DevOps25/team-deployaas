# ---- Stage 1: Build the application ----
FROM eclipse-temurin:24-jdk AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle/
COPY build.gradle .
COPY settings.gradle .

# Download dependencies (for caching)
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies

# Copy full project source
COPY . .

# Build application and run tests
RUN ./gradlew clean build --no-daemon

# ---- Stage 2: Create a minimal runtime image ----
FROM eclipse-temurin:24-jre
WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]