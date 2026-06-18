package com.betsettler.domain.model;

import com.betsettler.api.dto.EventOutcomeRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventOutcome {

	private String eventId;
	private String eventName;
	private String eventWinnerId;

	public EventOutcome(EventOutcomeRequest request) {
		this.eventId = request.getEventId();
		this.eventName = request.getEventName();
		this.eventWinnerId = request.getEventWinnerId();
	}
}
