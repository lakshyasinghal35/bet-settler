package com.betsettler.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.betsettler.domain.model.EventOutcome;
import com.betsettler.messaging.kafka.producer.KafkaMessageProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EventOutcomePublishingService {

	private static final Logger log = LoggerFactory.getLogger(EventOutcomePublishingService.class);

	private final KafkaMessageProducer kafkaMessageProducer;
	private final ObjectMapper objectMapper;
	private final String eventOutcomesTopic;

	public EventOutcomePublishingService(
			KafkaMessageProducer kafkaMessageProducer,
			ObjectMapper objectMapper,
			@Value("${app.kafka.topics.event-outcomes}") String eventOutcomesTopic) {
		this.kafkaMessageProducer = kafkaMessageProducer;
		this.objectMapper = objectMapper;
		this.eventOutcomesTopic = eventOutcomesTopic;
	}

	public void processEventOutcome(EventOutcome outcome) {
		try {
			String payload = objectMapper.writeValueAsString(outcome);
			kafkaMessageProducer.sendMessage(eventOutcomesTopic, outcome.getEventId(), payload);
		}
		catch (JsonProcessingException ex) {
			log.error("Failed to serialize event outcome eventId={}", outcome.getEventId(), ex);
		}
	}

}
