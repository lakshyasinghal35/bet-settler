# Step 1: Use an official OpenJDK base image from Docker Hub
FROM amazoncorretto:17

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3: Copy the Spring Boot JAR file into the container
COPY target/bet-settler-0.0.1-SNAPSHOT.jar /app/bet-settler.jar

# Step 4: Expose the port your application runs on
EXPOSE 8080

# Step 5: Define the command to run your Spring Boot application. This will be used when you run the docker image using docker run or directly from dashboard.
CMD ["java", "-jar", "/app/bet-settler.jar"]