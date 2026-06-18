package com.betsettler.messaging.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.betsettler.domain.model.EventOutcome;
import com.betsettler.domain.service.BetSettlementService;

@Component
public class EventOutcomeKafkaConsumer {

	private static final Logger log = LoggerFactory.getLogger(EventOutcomeKafkaConsumer.class);

	private final BetSettlementService betSettlementService;
	private final String eventOutcomesTopic;

	public EventOutcomeKafkaConsumer(
			BetSettlementService betSettlementService,
			@Value("${app.kafka.topics.event-outcomes}") String eventOutcomesTopic) {
		this.betSettlementService = betSettlementService;
		this.eventOutcomesTopic = eventOutcomesTopic;
	}

	@KafkaListener(
			topics = "${app.kafka.topics.event-outcomes}",
			groupId = "${spring.kafka.consumer.group-id}",
			containerFactory = "eventOutcomeKafkaListenerContainerFactory")
	public void consume(EventOutcome outcome) {
		log.info("Received event outcome from topic={} eventId={}", eventOutcomesTopic, outcome.getEventId());
		betSettlementService.settle(outcome);
	}

}
