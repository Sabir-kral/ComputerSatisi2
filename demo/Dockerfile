# Dockerfile
FROM eclipse-temurin:21-jdk

# Spring Boot jar və config əlavə et
COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]