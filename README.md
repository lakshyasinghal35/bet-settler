# Bet Settler

Backend application to simulate sports betting event outcomes — create bets, publish event results to Kafka, and settle bets via Redis-backed storage.

## Getting Started

Use this guide when cloning the project onto a new machine.

### Prerequisites

| Tool | Version / notes |
|---|---|
| **Java** | **17** (required). The project targets Java 17. Using a newer JDK (e.g. Java 25) with Maven can cause Lombok annotation processing to fail and `mvn package` to error with many `cannot find symbol` messages for getters/setters. |
| **Maven** | 3.9+ — or use the included wrapper: `./mvnw` |
| **Docker + Docker Compose** | Runs Kafka and Redis locally |
| **Postman** (optional) | For testing the REST APIs |

Verify Java before building:

```bash
java -version   # should report 17.x
mvn -version    # should also report Java 17
```

If Maven picks up the wrong JDK, point it at Java 17:

```bash
export JAVA_HOME=/path/to/jdk-17
export PATH="$JAVA_HOME/bin:$PATH"
```

On macOS with Homebrew OpenJDK 17:

```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
```

### 1. Clone the repository

```bash
git clone <your-repo-url>
cd bet-settler
```

### 2. Update machine-specific configuration

The repo may contain a hardcoded IP (e.g. `192.168.1.9`) from a previous machine. Replace it with an address this machine can use to reach the Docker-hosted Kafka and Redis services.

**`src/main/resources/application.properties`**
(Can skip if running the jar on the same machine hosting Kafka/Redis)
```properties
spring.kafka.bootstrap-servers=<your-ip-address>:9092
spring.data.redis.host=<your-ip-address>
```

**`docker-compose.yml`**

```yaml
KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://<your-ip-address>:9092
```

**`localhost` vs IP address:** Kafka returns its *advertised* listener address to clients. If the app runs on the host (not inside Docker), `localhost` often does not work because Kafka advertises a different address. Use your machine's LAN IP (e.g. `192.168.x.x`) in all three places above. RocketMQ is not required — bet settlement publishing is mocked and only logs to the console.

### 3. Start infrastructure (Kafka + Redis)

```bash
docker compose up -d
```

This starts:

- **Redis** on port `6379`
- **Kafka** on port `9092`
- **Kafka UI** on port `8090` (optional dashboard at http://localhost:8090/)

Check containers are running:

```bash
docker compose ps
```

Wait a few seconds after startup before running the app so Kafka is ready.

### 4. Build the application

```bash
mvn clean package
# or
./mvnw clean package
```

The JAR is written to `target/bet-settler-0.0.1-SNAPSHOT.jar`.

### 5. Run the application

**Option A — Run locally (recommended for development)**

```bash
java -jar target/bet-settler-0.0.1-SNAPSHOT.jar
```

Or:

```bash
./mvnw spring-boot:run
```

The API is available at http://localhost:8080.

**Option B — Run via Docker**

Build the image (requires the JAR from step 4):

```bash
docker build -t bet-settler:latest .
docker run -p 8080:8080 bet-settler:latest
```

The Docker image contains only the application. Kafka and Redis still run via `docker compose`.

### 6. Test the APIs

Import the Postman collection:

```
postman/bet-settler-api.postman_collection.json
```

Suggested flow:

1. **Create Bets** — `POST /api/v1/bets` (request body includes all sample bets)
2. **Get Bets by Event ID** — `GET /api/v1/bets?eventId=Match 1`
3. **Publish Event Outcome** — `POST /api/v1/event-outcomes` (publishes to Kafka and triggers settlement)

Sample bet payloads are in `src/main/resources/data/sample-bets.json`.

### Quick checklist

```text
[ ] Java 17 installed and used by Maven
[ ] Docker running
[ ] application.properties updated with reachable IP/host
[ ] docker-compose.yml KAFKA_ADVERTISED_LISTENERS updated
[ ] docker compose up -d
[ ] mvn clean package
[ ] Application running on port 8080
[ ] Postman collection imported and tested
```

## Architecture (local)

```text
Postman  →  Spring Boot (:8080)  →  Redis
                              ↘  Kafka  →  consumer  →  settle bets
                              ↘  Mock RocketMQ (console logs only)
```
