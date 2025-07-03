# File: product-service/Dockerfile
FROM openjdk:21-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} product-service.jar
ENTRYPOINT ["java","-jar","/product-service.jar"]