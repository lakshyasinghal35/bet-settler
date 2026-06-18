package com.betsettler.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBetRequest {

	@NotBlank
	private String betId;

	@NotBlank
	private String userId;

	@NotBlank
	private String eventId;

	@NotBlank
	private String eventMarketId;

	@NotBlank
	private String eventWinnerId;

	@NotNull
	@Positive
	private BigDecimal betAmount;

}
