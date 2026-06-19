package com.betsettler.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventOutcomeRequest {

	@NotBlank
	private String eventId;

	@NotBlank
	private String eventName;

	@NotBlank
	private String eventWinnerId;

}
