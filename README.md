# Bet Settler
Backend application to simulate sports betting event outcome

## How To Run App On Local

### Step 1: Clone git repo to local machine

### Step 2: Edit the application.properties file and docker-compose.yml to point to your machine IP

In application.properties, edit the following:

spring.kafka.bootstrap-servers=<your-ip-address>:9092

spring.data.redis.host=<your-ip-address>

In docker-compose.yml, edit the following

KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://<your-ip-address>:9092

### Step 3: Generate the jar file using maven package command

mvn clean package

### Step 4: Generate the docker image

docker build -t bet-settler:{tag} .

### Step 4: Run the docker-compose file to start the kafka and redis containers

docker compose up -d

### Step 5: Run the docker image of the application

docker run -p {host-port}:{docker-port} bet-settler:{tag}


### Step 6: Use the postman collection APIs from the file mentioned below to test the APIs

bet-settler/postman/bet-settler-api.postman_collection.json

#### (Sample bets are provided in sample-bets.json file in the resources folder in case needed)




#### Additionally you can use Kafka UI to monitor the kafka topics and messages by using the following address in the browser

http://localhost:8090/



