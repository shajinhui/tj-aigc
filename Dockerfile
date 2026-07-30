FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /workspace/target/tj-aigc.jar /app/tj-aigc.jar

EXPOSE 8094
ENTRYPOINT ["java", "-jar", "/app/tj-aigc.jar"]
