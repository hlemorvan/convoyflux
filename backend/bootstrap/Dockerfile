FROM maven:3.9-eclipse-temurin-25-alpine AS builder
WORKDIR /app

# Copie les POM en premier pour tirer parti du cache des layers Docker
COPY pom.xml .
COPY domain/pom.xml domain/
COPY application/pom.xml application/
COPY infrastructure/pom.xml infrastructure/
COPY presentation/pom.xml presentation/
COPY bootstrap/pom.xml bootstrap/
RUN mvn dependency:go-offline -B -q

# Build sans tests
COPY domain/src domain/src/
COPY application/src application/src/
COPY infrastructure/src infrastructure/src/
COPY presentation/src presentation/src/
COPY bootstrap/src bootstrap/src/
RUN mvn package -Dmaven.test.skip=true -B -q -pl bootstrap -am

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /app/bootstrap/target/bootstrap-*.jar app.jar
EXPOSE 8080 7000
ENTRYPOINT ["java", "-jar", "app.jar"]
