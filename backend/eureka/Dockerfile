FROM eclipse-temurin:21-jdk
WORKDIR /app

# Tên file jar theo log build
COPY target/eureka-0.0.1-SNAPSHOT.jar app.jar

# Thường eureka dùng 8761, sửa lại nếu bạn config khác
EXPOSE 8762

ENTRYPOINT ["java", "-jar", "app.jar"]