package com.betsettler.domain.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betsettler.domain.model.Bet;
import com.betsettler.domain.model.BetSettlement;
import com.betsettler.domain.model.BetStatus;
import com.betsettler.domain.model.EventOutcome;
import com.betsettler.domain.model.SettlementStatus;
import com.betsettler.messaging.rocketmq.producer.BetSettlementProducer;
import com.betsettler.repository.BetRepository;

@Service
public class BetSettlementService {

	private static final Logger log = LoggerFactory.getLogger(BetSettlementService.class);

	private final BetRepository betRepository;
	private final BetSettlementProducer betSettlementProducer;

	public BetSettlementService(BetRepository betRepository, BetSettlementProducer betSettlementProducer) {
		this.betRepository = betRepository;
		this.betSettlementProducer = betSettlementProducer;
	}

	public void settle(EventOutcome outcome) {
		List<Bet> bets = betRepository.findByEventId(outcome.getEventId());
		if (bets.isEmpty()) {
			log.info("No bets found for eventId={}", outcome.getEventId());
			return;
		}

		for (Bet bet : bets) {
			settleBet(bet, outcome);
		}
	}

	private void settleBet(Bet bet, EventOutcome outcome) {
		if (bet.getStatus() == BetStatus.SETTLED) {
			log.debug("Skipping already settled betId={}", bet.getBetId());
			return;
		}

		Instant settledAt = Instant.now();
		boolean marked = betRepository.markSettledIfOpen(bet.getBetId(), settledAt);
		if (!marked) {
			log.debug("Bet already settled (concurrent or idempotent replay) betId={}", bet.getBetId());
			return;
		}

		SettlementStatus status = bet.getEventWinnerId().equals(outcome.getEventWinnerId())
				? SettlementStatus.WON
				: SettlementStatus.LOST;
		BigDecimal payoutAmount = status == SettlementStatus.WON
				? bet.getBetAmount().multiply(bet.getOdds())
				: BigDecimal.ZERO;

		BetSettlement settlement = new BetSettlement(
				bet.getBetId(),
				bet.getUserId(),
				bet.getEventId(),
				outcome.getEventName(),
				bet.getEventMarketId(),
				bet.getEventWinnerId(),
				outcome.getEventWinnerId(),
				bet.getBetAmount(),
				bet.getOdds(),
				payoutAmount,
				status,
				settledAt);

		betSettlementProducer.send(settlement);
		log.info("Settled betId={} status={} payoutAmount={}", bet.getBetId(), status, payoutAmount);
	}

}
