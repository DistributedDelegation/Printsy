# Use an official Java runtime as a parent image
FROM openjdk:17

# Set the working directory in the container
WORKDIR /app

# Copy the jar file from your target folder to the container
COPY target/backend-1.0.jar /app/backend.jar

# Make port 8085 available to the world outside this container
EXPOSE 8085

# Run the jar file 
ENTRYPOINT ["java", "-jar", "/app/backend.jar"]
