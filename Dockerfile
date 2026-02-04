# Build stage
FROM eclipse-temurin:25-jdk-alpine AS build
RUN apk add --no-cache maven
WORKDIR /app
COPY pom.xml .
# Download dependencies first (cached layer)
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

# Essential non-sensitive environment variables
ENV DB_URL=jdbc:postgresql://db:5432/mini_crm
ENV DB_USERNAME=postgres

ENTRYPOINT ["java", "-jar", "app.jar"]
