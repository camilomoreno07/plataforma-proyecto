FROM maven:latest AS maven

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:latest

WORKDIR /app

COPY --from=maven /app/target/plataforma-0.0.1-SNAPSHOT.jar ./plataforma.jar
CMD ["java", "-jar", "/app/plataforma.jar"]