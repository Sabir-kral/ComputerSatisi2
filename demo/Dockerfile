# 1-ci Mərhələ: Maven və Java 17 ilə kodu Docker-in daxilində yığırıq
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# 2-ci Mərhələ: Yalnız işlək jar faylını götürürük
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Lazımi qovluqları yaradırıq
RUN mkdir -p /app/config && mkdir -p /app/uploads
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]