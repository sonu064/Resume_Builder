# Use Java 17 image
FROM openjdk:17-jdk-slim

# Copy jar file into container
COPY target/resume-builder-0.1.0-SNAPSHOT.jar app.jar

# Run application
ENTRYPOINT ["java", "-jar", "/app.jar"]