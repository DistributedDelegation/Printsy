# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /usr/src/app
# This layer will be cached unless pom.xml changes.
COPY ./pom.xml .
RUN mvn dependency:go-offline
# This layer will be rebuilt every time the source code changes.
COPY ./src ./src
RUN mvn clean generate-resources package

# Stage 2: Create the runtime image
FROM openjdk:17-slim
COPY --from=builder /usr/src/app/target/transaction-worker-allinone.jar /usr/app/transaction-worker-allinone.jar
WORKDIR /usr/app
EXPOSE 8080
EXPOSE 50051

# Run the application
CMD ["java", "-jar", "transaction-worker-allinone.jar"]
