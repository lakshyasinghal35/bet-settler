package com.betsettler.messaging.kafka.consumer;

import com.betsettler.model.EventOutcome;
import com.betsettler.service.EventOutcomeProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessageConsumer {

	private static final Logger log = LoggerFactory.getLogger(KafkaMessageConsumer.class);

	private final EventOutcomeProcessingService eventOutcomeProcessingService;
	private final String eventOutcomesTopic;

	public KafkaMessageConsumer(
			EventOutcomeProcessingService eventOutcomeProcessingService,
			@Value("${app.kafka.topics.event-outcomes}") String eventOutcomesTopic) {
		this.eventOutcomeProcessingService = eventOutcomeProcessingService;
		this.eventOutcomesTopic = eventOutcomesTopic;
	}

	@KafkaListener(
			topics = "${app.kafka.topics.event-outcomes}",
			groupId = "${spring.kafka.consumer.group-id}",
			containerFactory = "eventOutcomeKafkaListenerContainerFactory")
	public void consume(EventOutcome outcome) {
		log.info("Received event outcome from topic={} eventId={}", eventOutcomesTopic, outcome.getEventId());
		eventOutcomeProcessingService.process(outcome);
	}

}
