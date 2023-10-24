FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/spring-boot-library-0.0.1-SNAPSHOT.jar spring-boot-library.jar
EXPOSE 8443
ENTRYPOINT ["java", "-jar", "spring-boot-library.jar"]
