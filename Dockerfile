# Étape 1 : Builder le proje
FROM gradle:8.5-jdk21 AS build

WORKDIR /home/gradle/project

COPY build.gradle settings.gradle gradlew gradle.properties ./
COPY gradle ./gradle

RUN ./gradlew --version

COPY src ./src

RUN ./gradlew bootJar --no-daemon

# Étape 2 : Image ultra légère
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]