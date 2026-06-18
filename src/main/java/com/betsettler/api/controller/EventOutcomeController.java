package com.betsettler.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.betsettler.api.dto.EventOutcomeRequest;
import com.betsettler.api.dto.EventOutcomeResponse;
import com.betsettler.domain.model.EventOutcome;
import com.betsettler.messaging.kafka.producer.EventOutcomeKafkaProducer;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/event-outcomes")
public class EventOutcomeController {

	private final EventOutcomeKafkaProducer eventOutcomeKafkaProducer;

	public EventOutcomeController(EventOutcomeKafkaProducer eventOutcomeKafkaProducer) {
		this.eventOutcomeKafkaProducer = eventOutcomeKafkaProducer;
	}

	@PostMapping
	public ResponseEntity<EventOutcomeResponse> publishEventOutcome(@Valid @RequestBody EventOutcomeRequest request) {
		EventOutcome outcome = new EventOutcome(
				request.getEventId(),
				request.getEventName(),
				request.getEventWinnerId());
		eventOutcomeKafkaProducer.publish(outcome);
		return ResponseEntity.status(HttpStatus.ACCEPTED)
				.body(new EventOutcomeResponse("Event outcome published", request.getEventId()));
	}

}
