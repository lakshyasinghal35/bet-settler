package com.betsettler.controller;

import com.betsettler.domain.service.EventOutcomePublishingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.betsettler.dto.EventOutcomeRequest;
import com.betsettler.dto.EventOutcomeResponse;
import com.betsettler.domain.model.EventOutcome;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/event-outcomes")
public class EventOutcomeController {
	private final EventOutcomePublishingService eventOutcomePublishingService;

	public EventOutcomeController(EventOutcomePublishingService eventOutcomePublishingService) {
		this.eventOutcomePublishingService = eventOutcomePublishingService;
	}

	@PostMapping
	public ResponseEntity<EventOutcomeResponse> publishEventOutcome(@Valid @RequestBody EventOutcomeRequest request) {
		eventOutcomePublishingService.processEventOutcome(new EventOutcome(request));
		return ResponseEntity.status(HttpStatus.ACCEPTED)
				.body(new EventOutcomeResponse("Event outcome published", request.getEventId()));
	}

}
