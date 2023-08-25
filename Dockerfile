FROM maven:3.9.2-amazoncorretto-17 AS build
WORKDIR /app
COPY src src
COPY pom.xml pom.xml
RUN mvn -f pom.xml clean package -Dmaven.test.skip=true
FROM amazoncorretto:17.0.5
COPY --from=build app/target/*.jar /repomanager.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar","/repomanager.jar"]