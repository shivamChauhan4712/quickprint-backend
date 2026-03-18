# 1. Build stage
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# 2. Run stage
FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/QuickPrint-0.0.1-SNAPSHOT.jar QuickPrint.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","QuickPrint.jar"]