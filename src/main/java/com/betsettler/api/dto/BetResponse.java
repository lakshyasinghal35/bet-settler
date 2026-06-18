package com.betsettler.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.betsettler.domain.model.Bet;
import com.betsettler.domain.model.BetStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BetResponse {

	private String betId;
	private String userId;
	private String eventId;
	private String eventMarketId;
	private String eventWinnerId;
	private BigDecimal betAmount;
	private BigDecimal odds;
	private BetStatus status;
	private Instant settledAt;

	public BetResponse(Bet bet) {
		betId = bet.getBetId();
		userId = bet.getUserId();
		eventId = bet.getEventId();
		eventMarketId = bet.getEventMarketId();
		eventWinnerId = bet.getEventWinnerId();
		betAmount = bet.getBetAmount();
		odds = bet.getOdds();
		status = bet.getStatus();
		settledAt = bet.getSettledAt();
	}

}
