package com.betsettler.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.betsettler.domain.model.EventOutcome;
import com.betsettler.messaging.kafka.producer.KafkaMessageProducer;

@Service
public class EventOutcomePublishingService {
	private static final Logger log = LoggerFactory.getLogger(EventOutcomePublishingService.class);

	private final KafkaMessageProducer kafkaMessageProducer;
	private final String eventOutcomesTopic;

	public EventOutcomePublishingService(
			KafkaMessageProducer kafkaMessageProducer,
			@Value("${app.kafka.topics.event-outcomes}") String eventOutcomesTopic) {
		this.kafkaMessageProducer = kafkaMessageProducer;
		this.eventOutcomesTopic = eventOutcomesTopic;
	}

	public void processEventOutcome(EventOutcome outcome) {
		log.info("Sending message with eventId={} to topic={}", outcome.getEventId(), eventOutcomesTopic);
		kafkaMessageProducer.sendMessage(eventOutcomesTopic, outcome.getEventId(), outcome);
	}

}
