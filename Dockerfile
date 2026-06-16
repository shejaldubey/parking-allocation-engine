# Stage 1: Build the application
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the final lightweight image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Expose the port (Render defaults to 10000, but Spring Boot usually uses 8080)
EXPOSE 8081

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
