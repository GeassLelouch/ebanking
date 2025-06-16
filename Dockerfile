# 第一階段：用 Maven 打包
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
# 複製 POM 與原始碼
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/transaction-service-*.jar transaction-service.jar
ENV SPRING_CONFIG_LOCATION=classpath:/,file:/config/
EXPOSE 8080
ENTRYPOINT ["java","-jar","transaction-service.jar"]