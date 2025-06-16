FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/transaction-service-1.0.0.jar transaction-service.jar
ENV SPRING_CONFIG_LOCATION=classpath:/,file:/config/
EXPOSE 8080
ENTRYPOINT ["java","-jar","transaction-service.jar"]
