# Use official OpenJDK runtime as parent image
FROM openjdk:17-jdk-alpine

# Set working directory in container
WORKDIR /app

# Copy Spring Boot jar file from target directory to the container
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose port
EXPOSE 8080

# Run Spring Boot app
ENTRYPOINT ["java", "-jar", "app/app.jar"]