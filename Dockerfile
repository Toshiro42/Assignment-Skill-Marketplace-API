# official maven image with JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy only pom and download dependencies (for caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build the application
COPY src ./src

# Build the jar
RUN mvn clean package -DskipTests

#Use an official JDK runtime image for the runtime stage
FROM eclipse-temurin:21-jre

#Set the working directory in the container to /app
WORKDIR /app

# Copy built jar from build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
