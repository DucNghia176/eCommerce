## File: product-service/Dockerfile
#FROM openjdk:21-jdk-slim
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} product-service.jar
#ENTRYPOINT ["java","-jar","/product-service.jar"]

FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY target/product-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
