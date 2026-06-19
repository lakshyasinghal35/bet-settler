package com.betsettler.domain.model;

import java.math.BigDecimal;

import com.betsettler.dto.CreateBetRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Bet {

	private String betId;
	private String userId;
	private String eventId;
	private String eventMarketId;
	private String eventWinnerId;
	private BigDecimal betAmount;
	private BetResult result;

	public Bet(CreateBetRequest req) {
		betId = req.getBetId();
		userId = req.getUserId();
		eventId = req.getEventId();
		eventMarketId = req.getEventMarketId();
		eventWinnerId = req.getEventWinnerId();
		betAmount = req.getBetAmount();
		this.result = null;
	}

}
