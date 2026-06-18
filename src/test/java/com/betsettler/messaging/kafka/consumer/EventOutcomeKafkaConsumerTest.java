package com.betsettler.messaging.kafka.consumer;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.betsettler.domain.model.EventOutcome;
import com.betsettler.domain.service.BetSettlementService;
import com.redis.testcontainers.RedisContainer;

@Testcontainers
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"event-outcomes"})
class EventOutcomeKafkaConsumerTest {

	@Container
	static RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("redis:7-alpine"));

	@Autowired
	private KafkaTemplate<String, EventOutcome> eventOutcomeKafkaTemplate;

	@MockBean
	private BetSettlementService betSettlementService;

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.redis.host", redisContainer::getHost);
		registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
	}

	@Test
	void consumesEventOutcomeAndDelegatesToSettlementService() {
		EventOutcome outcome = new EventOutcome("evt-123", "Team A vs Team B", "team-a");
		eventOutcomeKafkaTemplate.send("event-outcomes", outcome.getEventId(), outcome);

		verify(betSettlementService, timeout(5000)).settle(argThat(received ->
				"evt-123".equals(received.getEventId())
						&& "Team A vs Team B".equals(received.getEventName())
						&& "team-a".equals(received.getEventWinnerId())));
	}

}
