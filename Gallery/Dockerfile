# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /usr/src/app
# This layer will be cached unless pom.xml changes.
COPY ./pom.xml .
RUN mvn dependency:go-offline
# This layer will be rebuilt every time the source code changes.
COPY ./src ./src
RUN mvn clean package

# Stage 2: Create the runtime image
FROM openjdk:17-slim
COPY --from=builder /usr/src/app/target/gallery-allinone.jar /usr/app/gallery-allinone.jar
WORKDIR /usr/app
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "gallery-allinone.jar"]
