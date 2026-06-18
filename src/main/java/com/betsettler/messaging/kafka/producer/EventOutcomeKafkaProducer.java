package com.betsettler.messaging.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.betsettler.domain.model.EventOutcome;

@Component
public class EventOutcomeKafkaProducer {

	private static final Logger log = LoggerFactory.getLogger(EventOutcomeKafkaProducer.class);

	private final KafkaTemplate<String, EventOutcome> kafkaTemplate;
	private final String eventOutcomesTopic;

	public EventOutcomeKafkaProducer(
			KafkaTemplate<String, EventOutcome> kafkaTemplate,
			@Value("${app.kafka.topics.event-outcomes}") String eventOutcomesTopic) {
		this.kafkaTemplate = kafkaTemplate;
		this.eventOutcomesTopic = eventOutcomesTopic;
	}

	public void publish(EventOutcome outcome) {
		kafkaTemplate.send(eventOutcomesTopic, outcome.getEventId(), outcome);
		log.info("Published event outcome to topic={} eventId={}", eventOutcomesTopic, outcome.getEventId());
	}

}
