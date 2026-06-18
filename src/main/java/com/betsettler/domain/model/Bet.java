package com.betsettler.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

import com.betsettler.api.dto.CreateBetRequest;
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
	private BigDecimal odds;
	private BetStatus status;
	private Instant settledAt;

	public Bet(CreateBetRequest req) {
		betId = req.getBetId();
		userId = req.getUserId();
		eventId = req.getEventId();
		eventMarketId = req.getEventMarketId();
		eventWinnerId = req.getEventWinnerId();
		betAmount = req.getBetAmount();
		odds = req.getOdds();
		this.status = BetStatus.OPEN;
		this.settledAt = null;
	}
}
