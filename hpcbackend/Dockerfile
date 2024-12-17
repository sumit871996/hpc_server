# Use an official Maven image with OpenJDK 17 as the base image for both build and runtime
FROM maven:3.8.4-openjdk-17-slim AS build
# Set the working directory in the container
WORKDIR /app
# Copy the pom.xml and the project files to the container
COPY pom.xml . 
COPY src ./src
# Build the application using Maven (Java 17)
RUN mvn clean package -DskipTests

# Use the same OpenJDK 17 base image for the runtime as well
FROM openjdk:17-jdk-slim
# Set the working directory in the container
WORKDIR /app
# Copy the built JAR file from the previous stage to the container
COPY --from=build /app/target/hpcbackend-0.0.1-SNAPSHOT.jar .
# Set the command to run the application
CMD ["java", "-jar", "hpcbackend-0.0.1-SNAPSHOT.jar"]