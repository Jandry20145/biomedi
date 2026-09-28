FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY backend/pom.xml backend/mvnw backend/mvnw.cmd ./backend/
COPY backend/.mvn ./backend/.mvn
COPY database/migrations ./database/migrations
COPY database/migrations-postgresql ./database/migrations-postgresql
RUN mvn -f backend/pom.xml -B -DskipTests dependency:go-offline
COPY backend/src ./backend/src
RUN mvn -f backend/pom.xml -B clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S odontocare && adduser -S odontocare -G odontocare
COPY --from=build /workspace/backend/target/odontocare-api-*.jar app.jar
USER odontocare
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
