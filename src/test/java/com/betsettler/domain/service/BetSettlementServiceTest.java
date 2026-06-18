package com.betsettler.domain.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.betsettler.domain.model.Bet;
import com.betsettler.domain.model.BetStatus;
import com.betsettler.domain.model.EventOutcome;
import com.betsettler.domain.model.SettlementStatus;
import com.betsettler.messaging.rocketmq.producer.BetSettlementProducer;
import com.betsettler.repository.BetRepository;

@ExtendWith(MockitoExtension.class)
class BetSettlementServiceTest {

	@Mock
	private BetRepository betRepository;

	@Mock
	private BetSettlementProducer betSettlementProducer;

	@InjectMocks
	private BetSettlementService betSettlementService;

	@Test
	void settlesWinningBetWithPayout() {
		Bet bet = new Bet("bet-1", "user-1", "evt-1", "market-winner", "team-a",
				new BigDecimal("100.00"), new BigDecimal("2.50"), BetStatus.OPEN, null);
		when(betRepository.findByEventId("evt-1")).thenReturn(List.of(bet));
		when(betRepository.markSettledIfOpen(eq("bet-1"), any())).thenReturn(true);

		EventOutcome outcome = new EventOutcome("evt-1", "Team A vs Team B", "team-a");
		betSettlementService.settle(outcome);

		verify(betSettlementProducer).send(argThat(settlement ->
				settlement.getStatus() == SettlementStatus.WON
						&& settlement.getPayoutAmount().compareTo(new BigDecimal("250.00")) == 0
						&& settlement.getOdds().compareTo(new BigDecimal("2.50")) == 0));
	}

	@Test
	void settlesLosingBetWithZeroPayout() {
		//Use CreateBetRequest to create the bet

		Bet bet = new Bet("bet-2", "user-2", "evt-1", "market-winner", "team-b",
				new BigDecimal("50.00"), new BigDecimal("3.00"), BetStatus.OPEN, null);
		when(betRepository.findByEventId("evt-1")).thenReturn(List.of(bet));
		when(betRepository.markSettledIfOpen(eq("bet-2"), any())).thenReturn(true);

		EventOutcome outcome = new EventOutcome("evt-1", "Team A vs Team B", "team-a");
		betSettlementService.settle(outcome);

		verify(betSettlementProducer).send(argThat(settlement ->
				settlement.getStatus() == SettlementStatus.LOST
						&& settlement.getPayoutAmount().compareTo(BigDecimal.ZERO) == 0));
	}

	@Test
	void skipsAlreadySettledBet() {
		Bet bet = new Bet("bet-3", "user-3", "evt-1", "market-winner", "team-a",
				new BigDecimal("25.00"), new BigDecimal("1.80"), BetStatus.SETTLED, null);
		when(betRepository.findByEventId("evt-1")).thenReturn(List.of(bet));

		EventOutcome outcome = new EventOutcome("evt-1", "Team A vs Team B", "team-a");
		betSettlementService.settle(outcome);

		verify(betRepository, never()).markSettledIfOpen(any(), any());
		verify(betSettlementProducer, never()).send(any());
	}

	@Test
	void skipsWhenMarkSettledIfOpenFailsIdempotentReplay() {
		Bet bet = new Bet("bet-4", "user-4", "evt-1", "market-winner", "team-a",
				new BigDecimal("10.00"), new BigDecimal("2.00"), BetStatus.OPEN, null);
		when(betRepository.findByEventId("evt-1")).thenReturn(List.of(bet));
		when(betRepository.markSettledIfOpen(eq("bet-4"), any())).thenReturn(false);

		EventOutcome outcome = new EventOutcome("evt-1", "Team A vs Team B", "team-a");
		betSettlementService.settle(outcome);

		verify(betSettlementProducer, never()).send(any());
	}

	@Test
	void doesNothingWhenNoBetsFound() {
		when(betRepository.findByEventId("evt-missing")).thenReturn(List.of());

		EventOutcome outcome = new EventOutcome("evt-missing", "No Bets Event", "team-a");
		betSettlementService.settle(outcome);

		verify(betSettlementProducer, never()).send(any());
	}

}
