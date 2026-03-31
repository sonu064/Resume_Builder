# Use Java 17 image
FROM eclipse-temurin:17-jdk

# Copy jar file into container
COPY target/resume-builder-0.1.0-SNAPSHOT.jar app.jar

# Run application
ENTRYPOINT ["java", "-jar", "/app.jar"]