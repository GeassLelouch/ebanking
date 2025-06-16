# Stage 1: Build
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
COPY --from=build /app/target/transaction-service-1.0.0.jar /app/transaction-service.jar
ENV SPRING_CONFIG_LOCATION=classpath:/,file:/config/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/transaction-service.jar"]
