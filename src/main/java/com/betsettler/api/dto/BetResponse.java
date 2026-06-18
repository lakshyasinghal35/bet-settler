package com.betsettler.api.dto;

import java.math.BigDecimal;

import com.betsettler.domain.model.Bet;
import com.betsettler.domain.model.BetResult;

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
	private BetResult result;

	public BetResponse(Bet bet) {
		betId = bet.getBetId();
		userId = bet.getUserId();
		eventId = bet.getEventId();
		eventMarketId = bet.getEventMarketId();
		eventWinnerId = bet.getEventWinnerId();
		betAmount = bet.getBetAmount();
		result = bet.getResult();
	}

}
