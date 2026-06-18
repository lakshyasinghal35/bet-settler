package com.betsettler.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BetSettlement {

	private String betId;
	private String userId;
	private String eventId;
	private String eventName;
	private String eventMarketId;
	private String predictedWinnerId;
	private String actualWinnerId;
	private BigDecimal betAmount;
	private BigDecimal odds;
	private BigDecimal payoutAmount;
	private SettlementStatus status;
	private Instant settledAt;

}
