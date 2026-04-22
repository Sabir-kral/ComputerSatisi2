# 1. Mərhələ: Kodu yığmaq üçün Java 17 və Maven olan imic
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Öncə pom.xml-i kopyalayırıq ki, asılılıqları (dependencies) keşləsin
COPY pom.xml .
RUN mvn dependency:go-offline

# İndi bütün kodu kopyalayırıq və jar faylını yaradırıq
COPY src ./src
RUN mvn clean package -DskipTests

# 2. Mərhələ: Yalnız işlək jar faylını götürüb yüngül imic yaradırıq
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]