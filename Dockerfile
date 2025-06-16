# 第一階段：用 Maven 打包
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
# 複製 POM 與原始碼
COPY pom.xml .
COPY src ./src
# 編譯並打包（跳過測試加-DskipTests）
RUN mvn clean package

# 第二階段：只拷貝 jar 
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/transaction-service-*.jar transaction-service.jar
ENV SPRING_CONFIG_LOCATION=classpath:/,file:/config/
EXPOSE 8080
ENTRYPOINT ["java","-jar","transaction-service.jar"]