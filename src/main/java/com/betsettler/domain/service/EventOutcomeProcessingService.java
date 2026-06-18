package com.betsettler.domain.service;

import java.util.List;

import com.betsettler.domain.model.BetResult;
import com.betsettler.messaging.rocketmq.producer.BetSettlementProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betsettler.domain.model.Bet;
import com.betsettler.domain.model.EventOutcome;
import com.betsettler.repository.BetRepository;

@Service
public class EventOutcomeProcessingService {

	private static final Logger log = LoggerFactory.getLogger(EventOutcomeProcessingService.class);

	private final BetRepository betRepository;
	private final BetSettlementProducer betSettlementProducer;

	public EventOutcomeProcessingService(BetRepository betRepository, BetSettlementProducer betSettlementProducer) {
		this.betRepository = betRepository;
		this.betSettlementProducer = betSettlementProducer;
	}

	public void process(EventOutcome outcome) {
		List<Bet> bets = betRepository.findByEventId(outcome.getEventId());
		if (bets.isEmpty()) {
			log.info("No bets found for eventId={}", outcome.getEventId());
			return;
		}

		for (Bet bet : bets) {
			processBet(bet, outcome);
		}
	}

	private void processBet(Bet bet, EventOutcome outcome) {
		boolean won = bet.getEventWinnerId().equals(outcome.getEventWinnerId());
		bet.setResult(won ? BetResult.WON : BetResult.LOST);

		//publish to RocketMQ
		betSettlementProducer.send(bet);
		log.info("Processed betId={} eventId={} won={}", bet.getBetId(), outcome.getEventId(), won);
	}

}
