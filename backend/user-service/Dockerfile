FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY target/user-service-0.0.1-SNAPSHOT.jar app.jar

# Đổi cho đúng port thực tế của user-service
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]